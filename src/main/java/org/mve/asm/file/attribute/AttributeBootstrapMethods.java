package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.bootstrap.BootstrapMethod;

import java.util.Arrays;

public class AttributeBootstrapMethods extends Attribute
{
	public BootstrapMethod[] bootstrap = new BootstrapMethod[0];

	public void bootstrap(BootstrapMethod method)
	{
		this.bootstrap = Arrays.copyOf(this.bootstrap, this.bootstrap.length + 1);
		this.bootstrap[this.bootstrap.length-1] = method;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.BOOTSTRAP_METHODS;
	}

	@Override
	public int length()
	{
		int len = 8;
		for (BootstrapMethod s : this.bootstrap) len += s.length();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		b[0] = (byte) ((this.name >>> 8) & 0xFF);
		b[1] = (byte) (this.name & 0xFF);
		len -= 6;
		b[2] = (byte) ((len >>> 24) & 0xFF);
		b[3] = (byte) ((len >>> 16) & 0xFF);
		b[4] = (byte) ((len >>> 8) & 0xFF);
		b[5] = (byte) (len & 0xFF);
		b[6] = (byte) ((this.bootstrap.length >>> 8) & 0XFF);
		b[7] = (byte) (this.bootstrap.length & 0XFF);
		int index = 8;
		for (BootstrapMethod s : this.bootstrap)
		{
			int l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
