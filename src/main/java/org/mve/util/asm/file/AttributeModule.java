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
		int len = 16 + (2 * this.useCount) + (6 * this.requireCount);
		for (StructModuleExport s : this.exports) len += s.getLength();
		for (StructModuleOpen s : this.opens) len += s.getLength();
		for (StructModuleProvide s : this.provides) len += s.getLength();
		return len;
	}
}
