package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.Marker;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.file.AccessFlag;

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
		bytecode.addMethod(AccessFlag.ACC_PUBLIC, "getField", MethodType.methodType(Field.class).toMethodDescriptorString()).addCode()
			.addFieldInstruction(Opcodes.GETSTATIC, bytecode.getName(), "1", Generator.getSignature(AccessibleObject.class))
			.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(Field.class))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1);
	}

	@Override
	public void generate()
	{
		Class<?> type = this.field.getType();
		int modifiers = this.field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		MethodWriter getter = this.bytecode.addMethod(AccessFlag.ACC_PRIVATE, "0", MethodType.methodType(field.getType(), statics ? new Class[]{} : new Class[]{field.getDeclaringClass()}).toMethodDescriptorString());
		Generator.inline(getter);
		MethodWriter setter = this.bytecode.addMethod(AccessFlag.ACC_PRIVATE, "1", MethodType.methodType(void.class, statics ? new Class[]{field.getDeclaringClass()} : new Class[]{field.getDeclaringClass(), field.getType()}).toMethodDescriptorString());
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
		getterGenerator.generate(getter);
		setterGenerator.generate(setter);
		MethodWriter mw = this.bytecode.addMethod(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		Generator.inline(mw);
		CodeWriter code = mw.addCode();
		Marker marker = new Marker();
		code.addInstruction(Opcodes.ALOAD_1)
			.addJumpInstruction(Opcodes.IFNULL, marker)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(statics ? Opcodes.ICONST_1 : Opcodes.ICONST_2)
			.addJumpInstruction(Opcodes.IF_ICMPLT, marker);
		if (type.isPrimitive())
		{
			code.addInstruction(Opcodes.ALOAD_1)
				.addInstruction(statics ? Opcodes.ICONST_0 : Opcodes.ICONST_1)
				.addInstruction(Opcodes.AALOAD)
				.addJumpInstruction(Opcodes.IFNULL, marker);
		}
		code.addInstruction(Opcodes.ALOAD_0);
		if (statics)
		{
			code.addInstruction(Opcodes.ALOAD_1)
				.addInstruction(Opcodes.ICONST_0)
				.addInstruction(Opcodes.AALOAD);
		}
		else
		{
			code.addInstruction(Opcodes.ALOAD_1)
				.addInstruction(Opcodes.ICONST_0)
				.addInstruction(Opcodes.AALOAD)
				.addInstruction(Opcodes.ALOAD_1)
				.addInstruction(Opcodes.ICONST_1)
				.addInstruction(Opcodes.AALOAD);
		}
		Generator.unwarp(type, code);
		code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), setter.getName(), setter.getType(), false)
			.mark(marker)
			.addInstruction(Opcodes.ALOAD_0);
		if (!statics)
		{
			code.addInstruction(Opcodes.ALOAD_1)
				.addInstruction(Opcodes.ICONST_0)
				.addInstruction(Opcodes.AALOAD);
		}
		code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), getter.getName(), getter.getType(), false);
		Generator.warp(type, code);
		code.addInstruction(Opcodes.ARETURN)
			.setMaxs(statics ? 3 : 4, 2);
		if (statics)
		{
			mw = bytecode.addMethod(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString());
			Generator.inline(mw);
			(code = mw.addCode())
				.addInstruction(Opcodes.ALOAD_0)
				.addMethodInstruction(Opcodes.INVOKEVIRTUAL, this.bytecode.getName(), getter.getName(), getter.getType(), false);
			Generator.warp(type, code);
			code.addInstruction(Opcodes.ARETURN)
				.setMaxs(Generator.typeSize(type), 1);
		}
	}
}
