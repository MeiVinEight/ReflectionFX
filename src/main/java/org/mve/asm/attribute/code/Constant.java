package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.constant.ConstantValue;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Constant extends Instruction
{
	public final Object value;

	public Constant(Object value)
	{
		super(Opcodes.LDC);
		this.value = value;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		int i = ConstantValue.constant(pool, this.value);
		if (this.value instanceof Long || this.value instanceof Double)
		{
			array.write(Opcodes.LDC2_W);
			array.writeShort(i);
		}
		else if (i > 255)
		{
			array.write(Opcodes.LDC_W);
			array.writeShort(i);
		}
		else
		{
			array.write(Opcodes.LDC);
			array.write(i);
		}
	}
}
