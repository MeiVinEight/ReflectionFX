package org.mve.asm.attribute.code;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class FieldInstruction extends Instruction
{
	public final String type;
	public final String name;
	public final String desc;

	public FieldInstruction(int opcode, String type, String name, String desc)
	{
		super(opcode);
		this.type = type;
		this.name = name;
		this.desc = desc;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		array.writeShort(ConstantPoolFinder.findField(pool, this.type, this.name, this.desc));
	}
}
