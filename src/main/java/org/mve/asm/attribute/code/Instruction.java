package org.mve.asm.attribute.code;

import org.mve.asm.file.ConstantPool;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public abstract class Instruction implements Element
{
	public final int opcode;

	public Instruction(int opcode)
	{
		this.opcode = opcode;
	}

	@Override
	public void consume(ConstantPool pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		array.write(this.opcode);
	}
}
