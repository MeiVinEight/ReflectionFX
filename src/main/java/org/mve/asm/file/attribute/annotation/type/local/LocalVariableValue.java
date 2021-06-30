package org.mve.asm.file.attribute.annotation.type.local;

public class LocalVariableValue
{
	public int start;
	public int length;
	public int slot;

	public byte[] toByteArray()
	{
		return new byte[]
		{
			(byte) ((this.start >>> 8) & 0XFF),
			(byte) (this.start & 0XFF),
			(byte) ((this.length >>> 8) & 0XFF),
			(byte) (this.length & 0XFF),
			(byte) ((this.slot >>> 8) & 0XFF),
			(byte) (this.slot & 0XFF)
		};
	}
}
