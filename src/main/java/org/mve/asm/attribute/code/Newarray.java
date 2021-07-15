package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Newarray extends Instruction
{
	public int type;

	public Newarray(int type)
	{
		super(Opcodes.NEWARRAY);
		this.type = type;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		array.write(this.type);
	}
}
