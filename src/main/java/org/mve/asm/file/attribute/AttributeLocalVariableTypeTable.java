package org.mve.asm.file.attribute;

public class AttributeLocalVariableTypeTable extends AttributeLocalVariableTable
{
	@Override
	public AttributeType type()
	{
		return AttributeType.LOCAL_VARIABLE_TYPE_TABLE;
	}
}
