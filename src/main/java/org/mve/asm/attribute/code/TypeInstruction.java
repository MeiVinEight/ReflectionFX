package org.mve.asm.attribute.code;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class TypeInstruction extends Instruction
{
	public final String type;

	public TypeInstruction(int opcode, String type)
	{
		super(opcode);
		this.type = type;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		array.writeShort(ConstantPoolFinder.findClass(pool, this.type));
	}
}
