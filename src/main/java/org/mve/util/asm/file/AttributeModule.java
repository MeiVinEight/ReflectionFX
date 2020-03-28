package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeModule extends Attribute
{
	private short moduleNameIndex;
	private short moduleFlags;
	private short moduleVersionIndex;
	private short requireCount;
	private StructModuleRequire[] requires = new StructModuleRequire[0];
	private short exportCount;
	private StructModuleExport[] exports = new StructModuleExport[0];
	private short openCount;
	private StructModuleOpen[] opens = new StructModuleOpen[0];
	private short useCount;
	private short[] uses = new short[0];
	private short provideCount;
	private StructModuleProvide[] provides = new StructModuleProvide[0];

	public AttributeModule(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getModuleNameIndex()
	{
		return moduleNameIndex;
	}

	public void setModuleNameIndex(short moduleNameIndex)
	{
		this.moduleNameIndex = moduleNameIndex;
	}

	public short getModuleFlags()
	{
		return moduleFlags;
	}

	public void setModuleFlags(short moduleFlags)
	{
		this.moduleFlags = moduleFlags;
	}

	public short getModuleVersionIndex()
	{
		return moduleVersionIndex;
	}

	public void setModuleVersionIndex(short moduleVersionIndex)
	{
		this.moduleVersionIndex = moduleVersionIndex;
	}

	public short getModuleRequireCount()
	{
		return requireCount;
	}

	public void addModuleRequire(StructModuleRequire require)
	{
		StructModuleRequire[] arr = new StructModuleRequire[this.requireCount+1];
		System.arraycopy(this.requires, 0, arr, 0, this.requireCount);
		arr[this.requireCount] = Objects.requireNonNull(require);
		this.requires = arr;
		this.requireCount++;
	}

	public void setModuleRequire(int index, StructModuleRequire require)
	{
		this.requires[index] = Objects.requireNonNull(require);
	}

	public StructModuleRequire getModuleRequire(int index)
	{
		return this.requires[index];
	}

	public short getModuleExportCount()
	{
		return exportCount;
	}

	public void addModuleExport(StructModuleExport export)
	{
		StructModuleExport[] arr = new StructModuleExport[this.exportCount+1];
		System.arraycopy(this.exports, 0, arr, 0, this.exportCount);
		arr[this.exportCount] = Objects.requireNonNull(export);
		this.exports = arr;
		this.exportCount++;
	}

	public void setModuleExport(int index, StructModuleExport export)
	{
		this.exports[index] = Objects.requireNonNull(export);
	}

	public StructModuleExport getModuleExport(int index)
	{
		return this.exports[index];
	}

	public short getModuleOpenCount()
	{
		return openCount;
	}

	public void addModuleOpen(StructModuleOpen open)
	{
		StructModuleOpen[] arr = new StructModuleOpen[this.openCount+1];
		System.arraycopy(this.opens, 0, arr, 0, this.openCount);
		arr[this.openCount] = Objects.requireNonNull(open);
		this.opens = arr;
		this.openCount++;
	}

	public void setModuleOpen(int index, StructModuleOpen open)
	{
		this.opens[index] = Objects.requireNonNull(open);
	}

	public StructModuleOpen getModuleOpen(int index)
	{
		return this.opens[index];
	}

	public short getModuleUseCount()
	{
		return this.useCount;
	}

	public void addModuleUse(short cpIndex)
	{
		short[] arr = new short[this.useCount+1];
		System.arraycopy(this.uses, 0, arr, 0, this.useCount);
		arr[this.useCount] = cpIndex;
		this.uses = arr;
		this.useCount++;
	}

	public void setModuleUse(int index, short cpIndex)
	{
		this.uses[index] = cpIndex;
	}

	public short getModuleUse(int index)
	{
		return this.uses[index];
	}

	public short getModuleProvideCount()
	{
		return this.provideCount;
	}

	public void addModuleProvide(StructModuleProvide provide)
	{
		StructModuleProvide[] arr = new StructModuleProvide[this.provideCount+1];
		System.arraycopy(this.provides, 0, arr, 0, this.provideCount);
		arr[this.provideCount] = Objects.requireNonNull(provide);
		this.provides = arr;
		this.provideCount++;
	}

	public void setModuleProvide(int index, StructModuleProvide provide)
	{
		this.provides[index] = Objects.requireNonNull(provide);
	}

	public StructModuleProvide getModuleProvide(int index)
	{
		return this.provides[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.MODULE;
	}

	@Override
	public int getLength()
	{
		int len = 22 + (2 * this.useCount) + (6 * this.requireCount);
		for (StructModuleExport s : this.exports) len += s.getLength();
		for (StructModuleOpen s : this.opens) len += s.getLength();
		for (StructModuleProvide s : this.provides) len += s.getLength();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.moduleNameIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.moduleNameIndex & 0XFF);
		b[index++] = (byte) ((this.moduleFlags >>> 8) & 0XFF);
		b[index++] = (byte) (this.moduleFlags & 0XFF);
		b[index++] = (byte) ((this.moduleVersionIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.moduleVersionIndex & 0XFF);
		b[index++] = (byte) ((this.requireCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.requireCount & 0XFF);
		for (StructModuleRequire s : this.requires)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 6);
			index+=6;
		}
		b[index++] = (byte) ((this.exportCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.exportCount & 0XFF);
		for (StructModuleExport s : this.exports)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		b[index++] = (byte) ((this.openCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.openCount & 0XFF);
		for (StructModuleOpen s : this.opens)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index += l;
		}
		b[index++] = (byte) ((this.useCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.useCount & 0XFF);
		for (short s : this.uses)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		b[index++] = (byte) ((this.provideCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.provideCount & 0XFF);
		for (StructModuleProvide s : this.provides)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
