package org.mve.asm.attribute;

import org.mve.asm.attribute.code.local.LocalVariable;
import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.Attribute;
import org.mve.asm.file.AttributeLocalVariableTable;
import org.mve.asm.file.AttributeType;
import org.mve.asm.file.ConstantPool;

import java.util.Arrays;

public class LocalVariableTableWriter implements AttributeWriter
{
	private LocalVariable[] locals = new LocalVariable[0];

	public LocalVariableTableWriter localVariable(Marker from, Marker to, int slot, String name, String type)
	{
		int i = this.locals.length;
		this.locals = Arrays.copyOf(this.locals, i+1);
		this.locals[i] = new LocalVariable(from, to, slot, name, type);
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantPool pool)
	{
		AttributeLocalVariableTable lvt = new AttributeLocalVariableTable((short) ConstantPoolFinder.findUTF8(pool, AttributeType.LOCAL_VARIABLE_TABLE.getName()));
		for (LocalVariable local : this.locals)
		{
			lvt.addLocalVariableTable(local.transform(pool));
		}
		return lvt;
	}
}
