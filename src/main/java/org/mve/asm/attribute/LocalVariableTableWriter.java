package org.mve.asm.attribute;

import org.mve.asm.attribute.code.local.LocalVariable;
import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeLocalVariableTable;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class LocalVariableTableWriter implements AttributeWriter
{
	public LocalVariable[] local = new LocalVariable[0];

	public LocalVariableTableWriter variable(Marker from, Marker to, int slot, String name, String type)
	{
		int i = this.local.length;
		this.local = Arrays.copyOf(this.local, i+1);
		this.local[i] = new LocalVariable(from, to, slot, name, type);
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeLocalVariableTable lvt = new AttributeLocalVariableTable();
		lvt.name = ConstantPoolFinder.findUTF8(pool, AttributeType.LOCAL_VARIABLE_TABLE.getName());
		for (LocalVariable local : this.local)
		{
			lvt.local(local.transform(pool));
		}
		return lvt;
	}
}
