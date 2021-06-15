package org.mve.asm.attribute.code.local;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StructLocalVariableTable;

public class LocalVariable
{
	private final Marker from;
	private final Marker to;
	private final int slot;
	private final String name;
	private final String type;

	public LocalVariable(Marker from, Marker to, int slot, String name, String type)
	{
		this.from = from;
		this.to = to;
		this.slot = slot;
		this.name = name;
		this.type = type;
	}

	public StructLocalVariableTable transform(ConstantPool pool)
	{
		StructLocalVariableTable lvt = new StructLocalVariableTable();
		lvt.setStartPc((short) this.from.address);
		lvt.setLength((short) (this.to.address - this.from.address));
		lvt.setIndex((short) this.slot);
		lvt.setNameIndex((short) ConstantPoolFinder.findUTF8(pool, this.name));
		lvt.setDescriptorIndex((short) ConstantPoolFinder.findUTF8(pool, this.type));
		return lvt;
	}
}
