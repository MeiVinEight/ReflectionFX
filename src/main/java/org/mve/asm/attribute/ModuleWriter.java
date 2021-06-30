package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.module.Export;
import org.mve.asm.attribute.module.Open;
import org.mve.asm.attribute.module.Provide;
import org.mve.asm.attribute.module.Require;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeModule;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.attribute.module.ModuleExport;
import org.mve.asm.file.attribute.module.ModuleOpen;
import org.mve.asm.file.attribute.module.ModuleProvide;
import org.mve.asm.file.attribute.module.ModuleRequire;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class ModuleWriter implements AttributeWriter
{
	public String name;
	public int flag;
	public String version;
	public Require[] require = new Require[0];
	public Export[] export = new Export[0];
	public Open[] open = new Open[0];
	public String[] use = new String[0];
	public Provide[] provide = new Provide[0];

	public ModuleWriter name(String name)
	{
		this.name = name;
		return this;
	}

	public ModuleWriter flag(int flag)
	{
		this.flag = flag;
		return this;
	}

	public ModuleWriter version(String version)
	{
		this.version = version;
		return this;
	}

	public ModuleWriter require(Require require)
	{
		this.require = Arrays.copyOf(this.require, this.require.length+1);
		this.require[this.require.length-1] = require;
		return this;
	}

	public ModuleWriter export(Export export)
	{
		this.export = Arrays.copyOf(this.export, this.export.length+1);
		this.export[this.export.length-1] = export;
		return this;
	}

	public ModuleWriter open(Open open)
	{
		this.open = Arrays.copyOf(this.open, this.open.length+1);
		this.open[this.open.length-1] = open;
		return this;
	}

	public ModuleWriter use(String use)
	{
		this.use = Arrays.copyOf(this.use, this.use.length+1);
		this.use[this.use.length-1] = use;
		return this;
	}

	public ModuleWriter provide(Provide provide)
	{
		this.provide = Arrays.copyOf(this.provide, this.provide.length+1);
		this.provide[this.provide.length-1] = provide;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeModule module = new AttributeModule();
		module.name = ConstantPoolFinder.findUTF8(pool, AttributeType.MODULE.getName());

		module.module = ConstantPoolFinder.findModule(pool, this.name);
		module.flag = this.flag;
		module.version = ConstantPoolFinder.findUTF8(pool, this.version);

		for (Require require : this.require)
		{
			ModuleRequire r = new ModuleRequire();
			r.require = ConstantPoolFinder.findModule(pool, require.name);
			r.flag = require.flag;
			r.version = ConstantPoolFinder.findUTF8(pool, require.version);
			module.require(r);
		}

		for (Export export : this.export)
		{
			ModuleExport e = new ModuleExport();
			e.export = ConstantPoolFinder.findPackage(pool, export.name);
			e.flag = export.flag;
			String[] to = export.to;
			for (String s : to)
			{
				e.to(ConstantPoolFinder.findModule(pool, s));
			}
			module.export(e);
		}

		for (Open open : this.open)
		{
			ModuleOpen o = new ModuleOpen();
			o.open = ConstantPoolFinder.findPackage(pool, open.name);
			o.flag = open.flag;
			String[] to = open.to;
			for (String s : to)
			{
				o.to(ConstantPoolFinder.findModule(pool, s));
			}
			module.open(o);
		}

		for (String use : this.use)
		{
			module.use(ConstantPoolFinder.findClass(pool, use));
		}

		for (Provide provide : this.provide)
		{
			ModuleProvide p = new ModuleProvide();
			p.provide = ConstantPoolFinder.findClass(pool, provide.name);
			String[] with = provide.with;
			for (String s : with)
			{
				p.with(ConstantPoolFinder.findClass(pool, s));
			}
			module.provide(p);
		}

		return module;
	}
}
