package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Number extends Instruction
{
	public final int num;

	public Number(int opcode, int num)
	{
		super(opcode);
		this.num = num;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		if (this.opcode == Opcodes.BIPUSH) array.write(this.num);
		else if (this.opcode == Opcodes.SIPUSH) array.writeShort(this.num);
	}
}
