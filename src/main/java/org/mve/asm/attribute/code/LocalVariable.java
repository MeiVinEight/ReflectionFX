package org.mve.asm.attribute.code;

import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class LocalVariable extends Instruction
{
	public final int index;

	public LocalVariable(int opcode, int index)
	{
		super(opcode);
		this.index = index;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		if (wide[0])
		{
			array.writeShort(this.index);
		}
		else
		{
			array.write(this.index);
		}
		wide[0] = false;
	}
}
