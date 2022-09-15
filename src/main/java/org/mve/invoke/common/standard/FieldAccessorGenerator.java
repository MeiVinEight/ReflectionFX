package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.StackMapTableWriter;
import org.mve.asm.attribute.code.Marker;
import org.mve.invoke.FieldAccessor;
import org.mve.invoke.ModuleAccess;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldAccessorGenerator extends AccessibleObjectAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Field field;
	private final int argument;
	private final Object[] access = new Object[4];

	public FieldAccessorGenerator(Field field, Object[] argument)
	{
		super(field, field.getDeclaringClass(), argument);
		this.field = field;
		this.bytecode.interfaces = new String[]{Generator.type(FieldAccessor.class)};
		this.argument = argument.length;
		access[0] = JavaVM.random();
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, FieldAccessor.FIELD, MethodType.methodType(Field.class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, bytecode.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_OBJECTIVE], Generator.signature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.type(Field.class))
				.instruction(Opcodes.ARETURN)
				.max(1, 1)
			)
		);
	}

	@Override
	public void generate()
	{
		super.generate();

		Class<?> type = this.field.getType();
		int modifiers = this.field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);

		String set = JavaVM.random();
		String get = JavaVM.random();

		String getType = MethodType
			.methodType(type, statics ? new Class[0] : new Class[]{this.field.getDeclaringClass()})
			.toMethodDescriptorString();
		String setType = MethodType
			.methodType(void.class, statics ? new Class[]{type} : new Class[]{this.field.getDeclaringClass(), type})
			.toMethodDescriptorString();

		{
			FieldGetterGenerator getterGenerator;
			FieldSetterGenerator setterGenerator;
			if (Generator.anonymous(field.getDeclaringClass()))
			{
				getterGenerator = new UnsafeFieldGetterGenerator(field);
				setterGenerator = new UnsafeFieldSetterGenerator(field);
			}
			else
			{
				getterGenerator = new MagicFieldGetterGenerator(field);
				if (Modifier.isFinal(modifiers))
				{
					setterGenerator = new UnsafeFieldSetterGenerator(field);
				}
				else
				{
					setterGenerator = new MagicFieldSetterGenerator(field);
				}
			}

			this.access[1] = new ClassWriter()
				.set(
					Opcodes.version(8),
					AccessFlag.PUBLIC | AccessFlag.ABSTRACT | AccessFlag.INTERFACE,
					JavaVM.random(),
					Generator.type(Object.class),
					null
				)
				.method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC | AccessFlag.ABSTRACT,
						set,
						setType
					)
				)
				.method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC | AccessFlag.ABSTRACT,
						get,
						getType
					)
				);

			this.access[2] = new ClassWriter()
				.set(
					Opcodes.version(8),
					AccessFlag.PUBLIC,
					JavaVM.randomAnonymous(this.field.getDeclaringClass()),
					JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC],
					((ClassWriter) this.access[1]).name
				)
				.method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC | AccessFlag.SUPER,
						set,
						setType
					)
					.consume(m -> setterGenerator.generate(m, this.bytecode))
					.consume(Generator::inline)
				)
				.method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC,
						get,
						getType
					)
					.consume(m -> getterGenerator.generate(m, this.bytecode))
					.consume(Generator::inline)
				);

			byte[] code = ((ClassWriter) this.access[1]).toByteArray();
			Class<?> intf = Generator.UNSAFE.defineClass(null, code, 0, code.length, null, null);
			ModuleAccess.read(ModuleAccess.module(this.field.getDeclaringClass()), ModuleAccess.module(intf));
			code = ((ClassWriter) this.access[2]).toByteArray();
			Class<?> c = Generator.UNSAFE.defineAnonymousClass(this.field.getDeclaringClass(), code, null);
			this.access[3] = Generator.UNSAFE.allocateInstance(c);
		}

		this.bytecode
			.field(new FieldWriter()
				.set(
					AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL,
					(String) this.access[0],
					"L" + ((ClassWriter) this.access[1]).name + ";"
				)
			)
			.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.WITH, MethodType.methodType(FieldAccessor.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.consume(c -> Generator.merge(c, this.bytecode.name, argument))
				.field(Opcodes.GETSTATIC, this.bytecode.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_OBJECTIVE], Generator.signature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.type(Field.class))
				.instruction(Opcodes.ALOAD_1)
				.method(Opcodes.INVOKESTATIC, Generator.type(Generator.class), ReflectionAccessor.METHOD_GENERATE, MethodType.methodType(FieldAccessor.class, Field.class, Object[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ARETURN)
				.max(5, 3)
			)
		);

		Generator.with(this.bytecode, FieldAccessor.class);

		Marker marker = new Marker();
		this.bytecode.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.consume(c -> Generator.merge(c, this.bytecode.name, this.argument))
				.instruction(Opcodes.ALOAD_1)
				.jump(Opcodes.IFNULL, marker)
				.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ARRAYLENGTH)
				.instruction(statics ? Opcodes.ICONST_1 : Opcodes.ICONST_2)
				.jump(Opcodes.IF_ICMPLT, marker)
				.consume(c ->
				{
					if (type.isPrimitive())
					{
						c.instruction(Opcodes.ALOAD_1)
							.instruction(statics ? Opcodes.ICONST_0 : Opcodes.ICONST_1)
							.instruction(Opcodes.AALOAD)
							.jump(Opcodes.IFNULL, marker);
					}
				})
				.field(Opcodes.GETSTATIC, this.bytecode.name, (String) this.access[0], "L" + ((ClassWriter) this.access[1]).name + ";")
				.consume(c ->
				{
					if (statics)
					{
						c.instruction(Opcodes.ALOAD_1)
							.instruction(Opcodes.ICONST_0)
							.instruction(Opcodes.AALOAD);
					}
					else
					{
						c.instruction(Opcodes.ALOAD_1)
							.instruction(Opcodes.ICONST_0)
							.instruction(Opcodes.AALOAD)
							.type(Opcodes.CHECKCAST, Generator.type(this.field.getDeclaringClass()))
							.instruction(Opcodes.ALOAD_1)
							.instruction(Opcodes.ICONST_1)
							.instruction(Opcodes.AALOAD);
					}
					if (type.isPrimitive())
					{
						Generator.unwarp(type, c);
					}
					else
					{
						c.type(Opcodes.CHECKCAST, Generator.type(type));
					}
				})
				.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) this.access[1]).name, set, setType, true)
				.mark(marker)
				.field(Opcodes.GETSTATIC, this.bytecode.name, (String) this.access[0], "L" + ((ClassWriter) this.access[1]).name + ";")
				.consume(c ->
				{
					if (!statics)
					{
						c.instruction(Opcodes.ALOAD_1)
							.instruction(Opcodes.ICONST_0)
							.instruction(Opcodes.AALOAD)
							.type(Opcodes.CHECKCAST, Generator.type(this.field.getDeclaringClass()));
					}
				})
				.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) this.access[1]).name, get, getType, true)
				.consume(c -> Generator.warp(type, c))
				.instruction(Opcodes.ARETURN)
				.max(5, 3)
				.attribute(new StackMapTableWriter()
					.sameFrame(marker)
				)
			)
			.consume(Generator::inline)
		);

		if (statics)
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, FieldAccessor.GET, MethodType.methodType(Object.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) this.access[0], "L" + ((ClassWriter) this.access[1]).name + ";")
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) this.access[1]).name, get, getType, true)
					.consume(c -> Generator.warp(type, c)).instruction(Opcodes.ARETURN)
					.max(Generator.typeSize(type), 1)
				)
			);

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, FieldAccessor.SET, MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) this.access[0], "L" + ((ClassWriter) this.access[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.consume(c ->
					{
						if (type.isPrimitive())
						{
							Generator.unwarp(type, c);
						}
						else
						{
							c.type(Opcodes.CHECKCAST, Generator.type(type));
						}
					})
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) this.access[1]).name, set, setType, true)
					.instruction(Opcodes.RETURN)
					.max(1 + Generator.typeSize(type), 2)
				)
			);
		}
		else
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, FieldAccessor.GET, MethodType.methodType(Object.class, Object.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) this.access[0], "L" + ((ClassWriter) this.access[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.type(Opcodes.CHECKCAST, Generator.type(this.field.getDeclaringClass()))
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) this.access[1]).name, get, getType, true)
					.consume(c -> Generator.warp(type, c))
					.instruction(Opcodes.ARETURN)
					.max(2, 2)
				)
			);

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, FieldAccessor.SET, MethodType.methodType(void.class, Object.class, Object.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) this.access[0], "L" + ((ClassWriter) this.access[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.type(Opcodes.CHECKCAST, Generator.type(this.field.getDeclaringClass()))
					.instruction(Opcodes.ALOAD_2)
					.consume(c ->
					{
						if (type.isPrimitive())
						{
							Generator.unwarp(type, c);
						}
						else
						{
							c.type(Opcodes.CHECKCAST, Generator.type(type));
						}
					})
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) this.access[1]).name, set, setType, true)
					.instruction(Opcodes.RETURN)
					.max(3, 3)
				)
			);
		}
	}

	@Override
	public void postgenerate(Class<?> generated)
	{
		super.postgenerate(generated);
		Field field = ReflectionFactory.ACCESSOR.getField(generated, (String) this.access[0]);
		long offset = Generator.UNSAFE.staticFieldOffset(field);
		Generator.UNSAFE.putObject(generated, offset, this.access[3]);
	}
}
