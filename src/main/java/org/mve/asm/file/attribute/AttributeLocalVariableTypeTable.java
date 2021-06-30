package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.local.LocalVariableType;

import java.util.Arrays;

public class AttributeLocalVariableTypeTable extends Attribute
{
	private LocalVariableType[] local = new LocalVariableType[0];

	public void local(LocalVariableType local)
	{
		this.local = Arrays.copyOf(this.local, this.local.length + 1);
		this.local[this.local.length - 1] = local;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.LOCAL_VARIABLE_TYPE_TABLE;
	}

	@Override
	public int length()
	{
		return 8 + (10 * this.local.length);
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
		b[index++] = (byte) ((this.local.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.local.length & 0XFF);
		for (LocalVariableType s : this.local)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 10);
			index+=10;
		}
		return b;
	}
}
