package org.mve.invoke;

import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MagicFieldGetterGenerator extends FieldGetterGenerator
{
	public MagicFieldGetterGenerator(Field field)
	{
		super(field);
	}

	@Override
	public void generate(MethodWriter method)
	{
		Field field = this.getField();
		int modifiers = field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		CodeWriter code = method.addCode();
		if (statics)
		{
			code.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(field.getDeclaringClass()), field.getName(), Generator.getSignature(field.getType()))
				.setMaxs(Generator.typeSize(field.getType()), 1);
		}
		else
		{
			code.addInstruction(Opcodes.ALOAD_1)
				.addFieldInstruction(Opcodes.GETFIELD, Generator.getType(field.getDeclaringClass()), field.getName(), Generator.getSignature(field.getType()))
				.setMaxs(Generator.typeSize(field.getType()), 2);
		}
		Generator.returner(field.getType(), code);
	}
}
