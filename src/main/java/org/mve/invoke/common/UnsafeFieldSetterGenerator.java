package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class UnsafeFieldSetterGenerator extends FieldSetterGenerator
{
	public UnsafeFieldSetterGenerator(Field field)
	{
		super(field);
	}

	@Override
	public void generate(MethodWriter method, ClassWriter classWriter)
	{
		Field field = this.getField();
		int modifiers = field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		long off = statics ? ReflectionFactory.UNSAFE.staticFieldOffset(field) : ReflectionFactory.UNSAFE.objectFieldOffset(field);

		CodeWriter code = new CodeWriter();
		method.attribute(code);

		Class<?> type = field.getType();
		int load;
		if (type == byte.class || type == short.class || type == int.class || type == boolean.class || type == char.class)
		{
			load = Opcodes.ILOAD_1;
		}
		else if (type == long.class)
		{
			load = Opcodes.LLOAD_1;
		}
		else if (type == float.class)
		{
			load = Opcodes.FLOAD_1;
		}
		else if (type == double.class)
		{
			load = Opcodes.DLOAD_1;
		}
		else
		{
			load = Opcodes.ALOAD_1;
		}

		code.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class));
		if (statics)
		{
			code.field(Opcodes.GETSTATIC, classWriter.name, "0", Generator.getSignature(Class.class));
		}
		else
		{
			code.instruction(Opcodes.ALOAD_1);
			load++;
		}
		code.constant(off)
			.instruction(load);
		Generator.unsafeput(type, code);
		code.instruction(Opcodes.RETURN)
			.max(4 + Generator.typeSize(type), (statics ? 1 : 2) + Generator.typeSize(type));
	}
}
