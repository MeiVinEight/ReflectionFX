package org.mve.asm.file.attribute.module;

public class ModuleRequire
{
	public int require;
	public int flag;
	public int version;

	public byte[] toByteArray()
	{
		byte[] b = new byte[6];
		b[0] = (byte) ((this.require >>> 8) & 0XFF);
		b[1] = (byte) (this.require & 0XFF);
		b[2] = (byte) ((this.flag >>> 8) & 0XFF);
		b[3] = (byte) (this.flag & 0XFF);
		b[4] = (byte) ((this.version >>> 8) & 0XFF);
		b[5] = (byte) (this.version & 0XFF);
		return b;
	}
}
