package org.mve.asm.attribute.code;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.Opcodes;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.io.RandomAccessByteArray;

import java.util.Map;

public class Dynamic extends Instruction
{
	public int bootstrap;
	public String name;
	public String type;

	public Dynamic(int opcode, int bootstrap, String name, String type)
	{
		super(opcode);
		this.bootstrap = bootstrap;
		this.name = name;
		this.type = type;
	}

	@Override
	public void consume(ConstantArray pool, RandomAccessByteArray array, boolean[] wide, Map<int[], Marker> marker)
	{
		switch (this.opcode)
		{
			case Opcodes.LDC:
			{
				int dynamic = ConstantPoolFinder.findDynamic(pool, this.bootstrap, this.name, this.type);
				if (dynamic > 255)
				{
					array.write(Opcodes.LDC_W);
					array.writeShort(dynamic);
				}
				else
				{
					array.write(Opcodes.LDC);
					array.write(dynamic);
				}
				break;
			}
			case Opcodes.INVOKEDYNAMIC:
			{
				array.write(this.opcode);
				array.writeShort(ConstantPoolFinder.findInvokeDynamic(pool, this.bootstrap, this.name, this.type));
				array.writeShort(0);
				break;
			}
		}
	}
}
