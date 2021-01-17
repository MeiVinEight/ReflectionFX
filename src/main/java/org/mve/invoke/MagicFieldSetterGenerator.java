package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MagicFieldSetterGenerator extends FieldSetterGenerator
{
	public MagicFieldSetterGenerator(Field field)
	{
		super(field);
	}

	@Override
	public void generate(MethodWriter method, ClassWriter classWriter)
	{
		Field field = this.getField();
		int modifiers = field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);

		CodeWriter code = new CodeWriter();
		method.addAttribute(code);

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

		if (!statics)
		{
			code.addInstruction(Opcodes.ALOAD_1);
			load++;
		}

		code.addInstruction(load)
			.addFieldInstruction(statics ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, Generator.getType(field.getDeclaringClass()), ReflectionFactory.ACCESSOR.getName(field), Generator.getSignature(field.getType()))
			.addInstruction(Opcodes.RETURN)
			.setMaxs((statics ? 0 : 1) + Generator.typeSize(field.getType()), (statics ? 1 : 2) + Generator.typeSize(field.getType()));
	}
}
