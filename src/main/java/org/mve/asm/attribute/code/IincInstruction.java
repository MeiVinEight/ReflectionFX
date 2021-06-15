package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.file.ConstantPool;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class IincInstruction extends Instruction
{
	public final int indexbyte;
	public final int constbyte;

	public IincInstruction(int indexbyte, int constbyte)
	{
		super(Opcodes.IINC);
		this.indexbyte = indexbyte;
		this.constbyte = constbyte;
	}

	@Override
	public void consume(ConstantPool pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		if (wide[0])
		{
			array.writeShort(this.indexbyte);
			array.writeShort(this.constbyte);
		}
		else
		{
			array.write(this.indexbyte);
			array.write(this.constbyte);
		}
		wide[0] = false;
	}
}
