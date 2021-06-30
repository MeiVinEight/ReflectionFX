package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.module.ModuleExport;
import org.mve.asm.file.attribute.module.ModuleOpen;
import org.mve.asm.file.attribute.module.ModuleProvide;
import org.mve.asm.file.attribute.module.ModuleRequire;

import java.util.Arrays;

public class AttributeModule extends Attribute
{
	public int module;
	public int flag;
	public int version;
	public ModuleRequire[] require = new ModuleRequire[0];
	public ModuleExport[] export = new ModuleExport[0];
	public ModuleOpen[] open = new ModuleOpen[0];
	public int[] use = new int[0];
	public ModuleProvide[] provide = new ModuleProvide[0];

	public void require(ModuleRequire require)
	{
		this.require = Arrays.copyOf(this.require, this.require.length+1);
		this.require[this.require.length-1] = require;
	}

	public void export(ModuleExport export)
	{
		this.export = Arrays.copyOf(this.export, this.export.length+1);
		this.export[this.export.length-1] = export;
	}

	public void open(ModuleOpen open)
	{
		this.open = Arrays.copyOf(this.open, this.open.length+1);
		this.open[this.open.length-1] = open;
	}

	public void use(int use)
	{
		this.use = Arrays.copyOf(this.use, this.use.length+1);
		this.use[this.use.length-1] = use;
	}

	public void provide(ModuleProvide provide)
	{
		this.provide = Arrays.copyOf(this.provide, this.provide.length+1);
		this.provide[this.provide.length-1] = provide;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.MODULE;
	}

	@Override
	public int length()
	{
		int len = 22 + (2 * this.use.length) + (6 * this.require.length);
		for (ModuleExport s : this.export) len += s.length();
		for (ModuleOpen s : this.open) len += s.length();
		for (ModuleProvide s : this.provide) len += s.length();
		return len;
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
		b[index++] = (byte) ((this.module >>> 8) & 0XFF);
		b[index++] = (byte) (this.module & 0XFF);
		b[index++] = (byte) ((this.flag >>> 8) & 0XFF);
		b[index++] = (byte) (this.flag & 0XFF);
		b[index++] = (byte) ((this.version >>> 8) & 0XFF);
		b[index++] = (byte) (this.version & 0XFF);
		b[index++] = (byte) ((this.require.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.require.length & 0XFF);
		for (ModuleRequire s : this.require)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 6);
			index+=6;
		}
		b[index++] = (byte) ((this.export.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.export.length & 0XFF);
		for (ModuleExport s : this.export)
		{
			int l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		b[index++] = (byte) ((this.open.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.open.length & 0XFF);
		for (ModuleOpen s : this.open)
		{
			int l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index += l;
		}
		b[index++] = (byte) ((this.use.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.use.length & 0XFF);
		for (int s : this.use)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		b[index++] = (byte) ((this.provide.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.provide.length & 0XFF);
		for (ModuleProvide s : this.provide)
		{
			int l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
