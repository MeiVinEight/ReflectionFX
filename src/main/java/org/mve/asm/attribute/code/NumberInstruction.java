package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.file.ConstantPool;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class NumberInstruction extends Instruction
{
	public final int num;

	public NumberInstruction(int opcode, int num)
	{
		super(opcode);
		this.num = num;
	}

	@Override
	public void consume(ConstantPool pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		if (this.opcode == Opcodes.BIPUSH) array.write(this.num);
		else if (this.opcode == Opcodes.SIPUSH) array.writeShort(this.num);
	}
}
