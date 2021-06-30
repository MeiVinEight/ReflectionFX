package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.method.Argument;

import java.util.Arrays;

public class AttributeMethodArguments extends Attribute
{
	public Argument[] argument = new Argument[0];

	public void argument(Argument argument)
	{
		this.argument = Arrays.copyOf(this.argument, this.argument.length + 1);
		this.argument[this.argument.length-1] = argument;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.METHOD_PARAMETERS;
	}

	@Override
	public int length()
	{
		return 7 + (this.argument.length * 4);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) this.argument.length;
		for (Argument s : this.argument)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 4);
			index+=4;
		}
		return b;
	}
}
