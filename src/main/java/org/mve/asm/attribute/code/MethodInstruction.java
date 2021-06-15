package org.mve.asm.attribute.code;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.ConstantPool;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class MethodInstruction extends Instruction
{
	public final String type;
	public final String name;
	public final String desc;
	public final boolean isAbstract;

	public MethodInstruction(int opcode, String type, String name, String desc, boolean isAbstract)
	{
		super(opcode);
		this.type = type;
		this.name = name;
		this.desc = desc;
		this.isAbstract = isAbstract;
	}

	@Override
	public void consume(ConstantPool pool, RandomAccessByteArray array, boolean[] wide, Map<Integer, Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		array.writeShort(ConstantPoolFinder.findMethod(pool, this.type, this.name, this.desc, this.isAbstract));
		if (this instanceof InterfaceMethodInstruction)
		{
			array.write(((InterfaceMethodInstruction)this).count);
			array.write(0);
		}
	}
}
