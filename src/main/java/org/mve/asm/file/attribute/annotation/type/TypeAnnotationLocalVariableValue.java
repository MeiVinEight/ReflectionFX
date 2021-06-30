package org.mve.asm.file.attribute.annotation.type;

import org.mve.asm.file.attribute.annotation.type.local.LocalVariableValue;

import java.util.Arrays;

public class TypeAnnotationLocalVariableValue extends TypeAnnotationValue
{
	private LocalVariableValue[] value = new LocalVariableValue[0];

	public void local(LocalVariableValue value)
	{
		this.value = Arrays.copyOf(this.value, this.value.length + 1);
		this.value[this.value.length - 1] = value;
	}

	@Override
	public int length()
	{
		return 2 + (6 * this.value.length);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.value.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.value.length & 0XFF);
		for (LocalVariableValue s : this.value)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 6);
			index+=6;
		}
		return b;
	}
}
