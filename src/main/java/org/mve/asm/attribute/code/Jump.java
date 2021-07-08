package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Jump extends Instruction
{
	public final Marker marker;

	public Jump(int opcode, Marker marker)
	{
		super(opcode);
		this.marker = marker;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		int base = array.position();
		super.consume(pool, array, wide, marker);
		marker.put(new int[]{array.position(), this.opcode, base}, this.marker);
		if (this.opcode == Opcodes.GOTO_W)
		{
			array.writeInt(0);
		}
		else
		{
			array.writeShort(0);
		}
	}
}
