package org.mve.asm.attribute.code;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.Opcodes;
import org.mve.asm.Type;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class ConstantInstruction extends Instruction
{
	public final Object value;

	public ConstantInstruction(Object value)
	{
		super(Opcodes.LDC);
		this.value = value;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		if (value instanceof Number)
		{
			if (value instanceof Long)
			{
				long val = ((Number) value).longValue();
				int index = ConstantPoolFinder.findLong(pool, val);
				array.write(Opcodes.LDC2_W);
				array.writeShort(index);
			}
			else if (value instanceof Double)
			{
				double val = ((Number) value).doubleValue();
				int index = ConstantPoolFinder.findDouble(pool, val);
				array.write(Opcodes.LDC2_W);
				array.writeShort(index);
			}
			else if (value instanceof Float)
			{
				float val = ((Number) value).floatValue();
				int index = ConstantPoolFinder.findFloat(pool, val);
				if (index > 255)
				{
					array.write(Opcodes.LDC_W);
					array.writeShort(index);
				}
				else
				{
					array.write(Opcodes.LDC);
					array.write(index);
				}
			}
			else
			{
				int val = ((Number) value).intValue();
				int index = ConstantPoolFinder.findInteger(pool, val);
				if (index > 255)
				{
					array.write(Opcodes.LDC_W);
					array.writeShort(index);
				}
				else
				{
					array.write(Opcodes.LDC);
					array.write(index);
				}
			}
		}
		else if (value instanceof String)
		{
			String str = value.toString();
			int index = ConstantPoolFinder.findString(pool, str);
			if (index > 255)
			{
				array.write(Opcodes.LDC_W);
				array.writeShort(index);
			}
			else
			{
				array.write(Opcodes.LDC);
				array.write(index);
			}
		}
		else if (value instanceof Type)
		{
			String type = ((Type) value).getType();
			int index = ConstantPoolFinder.findClass(pool, type);
			if (index > 255)
			{
				array.write(Opcodes.LDC_W);
				array.writeShort(index);
			}
			else
			{
				array.write(Opcodes.LDC);
				array.write(index);
			}
		}
		else if (value instanceof Class)
		{
			Class<?> clazz = (Class<?>) value;
			int index = ConstantPoolFinder.findClass(pool, clazz.getTypeName().replace('.', '/'));
			if (index > 255)
			{
				array.write(Opcodes.LDC_W);
				array.writeShort(index);
			}
			else
			{
				array.write(Opcodes.LDC);
				array.write(index);
			}
		}
	}
}
