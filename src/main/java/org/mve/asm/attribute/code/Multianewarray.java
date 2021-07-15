package org.mve.asm.attribute.code;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.Opcodes;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Multianewarray extends Instruction
{
	public String type;
	public int dimension;

	public Multianewarray(String type, int dimension)
	{
		super(Opcodes.MULTIANEWARRAY);
		this.type = type;
		this.dimension = dimension;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		array.writeShort(ConstantPoolFinder.findClass(pool, this.type));
		array.write(this.dimension);
	}
}
