package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeBootstrapMethods extends Attribute
{
	private short bootstrapMethodCount;
	private StructBootstrapMethod[] bootstrapMethods = new StructBootstrapMethod[0];

	public AttributeBootstrapMethods(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getBootstrapMethodCount()
	{
		return bootstrapMethodCount;
	}

	public void addBootstrapMethod(StructBootstrapMethod method)
	{
		StructBootstrapMethod[] arr = new StructBootstrapMethod[this.bootstrapMethodCount+1];
		System.arraycopy(this.bootstrapMethods, 0, arr, 0, this.bootstrapMethodCount);
		arr[this.bootstrapMethodCount] = Objects.requireNonNull(method);
		this.bootstrapMethods = arr;
		this.bootstrapMethodCount++;
	}

	public void setBootstrapMethod(int index, StructBootstrapMethod method)
	{
		this.bootstrapMethods[index] = Objects.requireNonNull(method);
	}

	public StructBootstrapMethod getBootstrapMethod(int index)
	{
		return this.bootstrapMethods[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.BOOTSTRAP_METHODS;
	}

	@Override
	public int getLength()
	{
		int len = 8;
		for (StructBootstrapMethod s : this.bootstrapMethods) len += s.getLength();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		b[0] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[1] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[2] = (byte) ((len >>> 24) & 0XFF);
		b[3] = (byte) ((len >>> 16) & 0XFF);
		b[4] = (byte) ((len >>> 8) & 0XFF);
		b[5] = (byte) (len & 0XFF);
		b[6] = (byte) ((this.bootstrapMethodCount >>> 8) & 0XFF);
		b[7] = (byte) (this.bootstrapMethodCount & 0XFF);
		int index = 8;
		for (StructBootstrapMethod s : this.bootstrapMethods)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
