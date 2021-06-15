package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.FieldAccessor;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldAccessorGenerator extends AccessibleObjectAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Field field;

	public FieldAccessorGenerator(Field field)
	{
		super(field, field.getDeclaringClass());
		this.field = field;
		this.bytecode.setInterfaces(new String[]{Generator.getType(FieldAccessor.class)});
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.addMethod(new MethodWriter()
			.set(AccessFlag.ACC_PUBLIC, "getField", MethodType.methodType(Field.class).toMethodDescriptorString())
			.addAttribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, bytecode.getName(), "1", Generator.getSignature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.getType(Field.class))
				.instruction(Opcodes.ARETURN)
				.max(1, 1)
			)
		);
	}

	@Override
	public void generate()
	{
		Class<?> type = this.field.getType();
		int modifiers = this.field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		MethodWriter getter = new MethodWriter().set(AccessFlag.ACC_PRIVATE, "0", MethodType.methodType(field.getType(), statics ? new Class[]{} : new Class[]{field.getDeclaringClass()}).toMethodDescriptorString());
		this.bytecode.addMethod(getter);
		Generator.inline(getter);
		MethodWriter setter = new MethodWriter().set(AccessFlag.ACC_PRIVATE, "1", MethodType.methodType(void.class, statics ? new Class[]{field.getType()} : new Class[]{field.getDeclaringClass(), field.getType()}).toMethodDescriptorString());
		this.bytecode.addMethod(setter);
		Generator.inline(setter);
		FieldGetterGenerator getterGenerator;
		FieldSetterGenerator setterGenerator;
		if (Generator.isVMAnonymousClass(field.getDeclaringClass()))
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
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.addMethod(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.addAttribute(code);
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
		code.method(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), setter.getName(), setter.getType(), false)
			.mark(marker)
			.instruction(Opcodes.ALOAD_0);
		if (!statics)
		{
			code.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ICONST_0)
				.instruction(Opcodes.AALOAD);
		}
		code.method(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), getter.getName(), getter.getType(), false);
		Generator.warp(type, code);
		code.instruction(Opcodes.ARETURN)
			.max(statics ? 3 : 4, 2);
		if (statics)
		{
			mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString());
			bytecode.addMethod(mw);
			Generator.inline(mw);
			code = new CodeWriter();
			mw.addAttribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.method(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), getter.getName(), getter.getType(), false);
			Generator.warp(type, code);
			code.instruction(Opcodes.ARETURN)
				.max(Generator.typeSize(type), 1);
		}

		if (statics)
		{
			mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "get", MethodType.methodType(Object.class).toMethodDescriptorString());
			this.bytecode.addMethod(mw);
			code = new CodeWriter();
			mw.addAttribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.method(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), getter.getName(), getter.getType(), false);
			Generator.warp(type, code);
			code.instruction(Opcodes.ARETURN)
				.max(Generator.typeSize(type), 1);

			mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "set", MethodType.methodType(void.class, Object.class).toMethodDescriptorString());
			this.bytecode.addMethod(mw);
			code = new CodeWriter();
			mw.addAttribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ALOAD_1);
			Generator.unwarp(type, code);
			code.method(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), setter.getName(), setter.getType(), false)
				.instruction(Opcodes.RETURN)
				.max(1 + Generator.typeSize(type), 2);
		}
		else
		{
			mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "get", MethodType.methodType(Object.class, Object.class).toMethodDescriptorString());
			this.bytecode.addMethod(mw);
			code = new CodeWriter();
			mw.addAttribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ALOAD_1)
				.method(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), getter.getName(), getter.getType(), false);
			Generator.warp(type, code);
			code.instruction(Opcodes.ARETURN)
				.max(2, 2);

			mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "set", MethodType.methodType(void.class, Object.class, Object.class).toMethodDescriptorString());
			this.bytecode.addMethod(mw);
			code = new CodeWriter();
			mw.addAttribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ALOAD_2);
			Generator.unwarp(type, code);
			code.method(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), setter.getName(), setter.getType(), false)
				.instruction(Opcodes.RETURN)
				.max(3, 3);
		}
	}
}
