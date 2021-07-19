package org.mve.asm.attribute.annotation.type;

import org.mve.asm.attribute.annotation.type.local.Variable;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationLocalVariableValue;
import org.mve.asm.file.attribute.annotation.type.local.LocalVariableValue;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class LocalVariable extends TypeAnnotationValue<LocalVariable>
{
	public Variable[] variable = new Variable[0];

	public LocalVariable(int type)
	{
		super(type);
	}

	public LocalVariable()
	{
		super(0);
	}

	public LocalVariable variable(Marker from, Marker to, int slot)
	{
		this.variable = Arrays.copyOf(this.variable, this.variable.length+1);
		this.variable[this.variable.length-1] = new Variable(from, to, slot);
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationLocalVariableValue value = new TypeAnnotationLocalVariableValue();
		value.type = this.type;
		for (Variable variable : this.variable)
		{
			LocalVariableValue local = new LocalVariableValue();
			local.start = variable.from.address;
			local.length = variable.from.address = local.start;
			local.slot = variable.slot;
			value.local(local);
		}
		return value;
	}
}
