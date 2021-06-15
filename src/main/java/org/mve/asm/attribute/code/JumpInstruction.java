package org.mve.asm.attribute.code;

import org.mve.asm.Opcodes;
import org.mve.asm.file.ConstantPool;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class JumpInstruction extends Instruction
{
	public final Marker marker;

	public JumpInstruction(int opcode, Marker marker)
	{
		super(opcode);
		this.marker = marker;
	}

	@Override
	public void consume(ConstantPool pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		marker.put(array.position() - 1, this.marker);
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
