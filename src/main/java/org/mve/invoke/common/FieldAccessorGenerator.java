package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.code.Marker;
import org.mve.invoke.FieldAccessor;
import org.mve.invoke.ReflectionAccessor;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldAccessorGenerator extends AccessibleObjectAccessorGenerator
{
	private static final String GET = "0";
	private static final String SET = "1";
	private final ClassWriter bytecode = this.bytecode();
	private final Field field;
	private final int argument;

	public FieldAccessorGenerator(Field field, Object[] argument)
	{
		super(field, field.getDeclaringClass(), argument);
		this.field = field;
		this.bytecode.interfaces = new String[]{Generator.type(FieldAccessor.class)};
		this.argument = argument.length;
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, FieldAccessor.FIELD, MethodType.methodType(Field.class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, bytecode.name, Generator.CONSTANT_POOL[5], Generator.signature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.type(Field.class))
				.instruction(Opcodes.ARETURN)
				.max(1, 1)
			)
		);
	}

	@Override
	public void generate()
	{
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, ReflectionAccessor.WITH, MethodType.methodType(FieldAccessor.class, Object[].class).toMethodDescriptorString());
		CodeWriter code = new CodeWriter();
		Generator.merge(code, this.bytecode.name, argument);
		code.field(Opcodes.GETSTATIC, this.bytecode.name, Generator.CONSTANT_POOL[5], Generator.signature(AccessibleObject.class))
			.type(Opcodes.CHECKCAST, Generator.type(Field.class))
			.instruction(Opcodes.ALOAD_1)
			.method(Opcodes.INVOKESTATIC, Generator.type(Generator.class), Generator.CONSTANT_POOL[7], MethodType.methodType(FieldAccessor.class, org.mve.asm.attribute.code.Field.class, Object[].class).toMethodDescriptorString(), false)
			.instruction(Opcodes.ARETURN)
			.max(5, 3);
		mw.attribute(code);
		this.bytecode.method(mw);

		super.generate();
		Generator.with(this.bytecode, FieldAccessor.class);
		Class<?> type = this.field.getType();
		int modifiers = this.field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		MethodWriter getter = new MethodWriter().set(AccessFlag.PRIVATE, GET, MethodType.methodType(field.getType(), statics ? new Class[]{} : new Class[]{field.getDeclaringClass()}).toMethodDescriptorString());
		this.bytecode.method(getter);
		Generator.inline(getter);
		MethodWriter setter = new MethodWriter().set(AccessFlag.PRIVATE, SET, MethodType.methodType(void.class, statics ? new Class[]{field.getType()} : new Class[]{field.getDeclaringClass(), field.getType()}).toMethodDescriptorString());
		this.bytecode.method(setter);
		Generator.inline(setter);
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
		getterGenerator.generate(getter, this.bytecode);
		setterGenerator.generate(setter, this.bytecode);
		mw = new MethodWriter().set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.method(mw);
		Generator.inline(mw);
		code = new CodeWriter();
		mw.attribute(code);
		Generator.merge(code, this.bytecode.name, this.argument);
		Marker marker = new Marker();
		code.instruction(Opcodes.ALOAD_1)
			.jump(Opcodes.IFNULL, marker)
			.instruction(Opcodes.ALOAD_1)
			.instruction(Opcodes.ARRAYLENGTH)
			.instruction(statics ? Opcodes.ICONST_1 : Opcodes.ICONST_2)
			.jump(Opcodes.IF_ICMPLT, marker);
		if (type.isPrimitive())
		{
			code.instruction(Opcodes.ALOAD_1)
				.instruction(statics ? Opcodes.ICONST_0 : Opcodes.ICONST_1)
				.instruction(Opcodes.AALOAD)
				.jump(Opcodes.IFNULL, marker);
		}
		code.instruction(Opcodes.ALOAD_0);
		if (statics)
		{
			code.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ICONST_0)
				.instruction(Opcodes.AALOAD);
		}
		else
		{
			code.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ICONST_0)
				.instruction(Opcodes.AALOAD)
				.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ICONST_1)
				.instruction(Opcodes.AALOAD);
		}
		Generator.unwarp(type, code);
		code.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, setter.name, setter.type, false)
			.mark(marker)
			.instruction(Opcodes.ALOAD_0);
		if (!statics)
		{
			code.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ICONST_0)
				.instruction(Opcodes.AALOAD);
		}
		code.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, getter.name, getter.type, false);
		Generator.warp(type, code);
		code.instruction(Opcodes.ARETURN)
			.max(5, 3);

		if (statics)
		{
			mw = new MethodWriter().set(AccessFlag.PUBLIC, FieldAccessor.GET, MethodType.methodType(Object.class).toMethodDescriptorString());
			this.bytecode.method(mw);
			code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, getter.name, getter.type, false);
			Generator.warp(type, code);
			code.instruction(Opcodes.ARETURN)
				.max(Generator.typeSize(type), 1);

			mw = new MethodWriter().set(AccessFlag.PUBLIC, FieldAccessor.SET, MethodType.methodType(void.class, Object.class).toMethodDescriptorString());
			this.bytecode.method(mw);
			code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ALOAD_1);
			Generator.unwarp(type, code);
			code.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, setter.name, setter.type, false)
				.instruction(Opcodes.RETURN)
				.max(1 + Generator.typeSize(type), 2);
		}
		else
		{
			mw = new MethodWriter().set(AccessFlag.PUBLIC, FieldAccessor.GET, MethodType.methodType(Object.class, Object.class).toMethodDescriptorString());
			this.bytecode.method(mw);
			code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ALOAD_1)
				.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, getter.name, getter.type, false);
			Generator.warp(type, code);
			code.instruction(Opcodes.ARETURN)
				.max(2, 2);

			mw = new MethodWriter().set(AccessFlag.PUBLIC, FieldAccessor.SET, MethodType.methodType(void.class, Object.class, Object.class).toMethodDescriptorString());
			this.bytecode.method(mw);
			code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ALOAD_2);
			Generator.unwarp(type, code);
			code.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, setter.name, setter.type, false)
				.instruction(Opcodes.RETURN)
				.max(3, 3);
		}
	}
}
