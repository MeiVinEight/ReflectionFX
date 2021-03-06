package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Iinc extends Instruction
{
	public final int indexbyte;
	public final int constbyte;

	public Iinc(int indexbyte, int constbyte)
	{
		super(Opcodes.IINC);
		this.indexbyte = indexbyte;
		this.constbyte = constbyte;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
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
