package org.mve.asm.attribute.code.local;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;

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

	public org.mve.asm.file.attribute.local.LocalVariable transform(ConstantArray pool)
	{
		org.mve.asm.file.attribute.local.LocalVariable lvt = new org.mve.asm.file.attribute.local.LocalVariable();
		lvt.start = this.from.address;
		lvt.length = (this.to.address - this.from.address);
		lvt.slot = this.slot;
		lvt.name = ConstantPoolFinder.findUTF8(pool, this.name);
		lvt.type = ConstantPoolFinder.findUTF8(pool, this.type);
		return lvt;
	}
}
