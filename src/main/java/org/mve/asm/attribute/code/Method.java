package org.mve.asm.attribute.code;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.Opcodes;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Method extends Instruction
{
	public final String type;
	public final String name;
	public final String desc;
	public final boolean isAbstract;

	public Method(int opcode, String type, String name, String desc, boolean isAbstract)
	{
		super(opcode);
		this.type = type;
		this.name = name;
		this.desc = desc;
		this.isAbstract = isAbstract;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		super.consume(pool, array, wide, marker);
		array.writeShort(ConstantPoolFinder.findMethod(pool, this.type, this.name, this.desc, this.isAbstract));
		if (this.opcode == Opcodes.INVOKEINTERFACE)
		{
			array.write(((InterfaceMethod)this).count);
			array.write(0);
		}
	}
}
