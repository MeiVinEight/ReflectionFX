package org.mve.invoke.common.polymorphism;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.standard.MagicFieldGetterGenerator;
import org.mve.invoke.common.standard.MagicFieldSetterGenerator;
import org.mve.invoke.common.standard.UnsafeFieldSetterGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MagicPolymorphismFieldGenerator extends PolymorphismFieldGenerator
{
	public MagicPolymorphismFieldGenerator(Class<?> target, MethodKind implementation, String operation, int kind)
	{
		super(target, implementation, operation, kind);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		Field field = ReflectionFactory.ACCESSOR.getField(getTarget(), operation());
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		bytecode.method(mw);
		Generator.inline(mw);
		if (kind() == ReflectionFactory.KIND_PUT)
		{
			if (Modifier.isFinal(field.getModifiers()))
			{
				new UnsafeFieldSetterGenerator(field).generate(mw, bytecode);
			}
			else
			{
				new MagicFieldSetterGenerator(field).generate(mw, bytecode);
			}
		}
		else if (this.kind() == ReflectionFactory.KIND_GET)
		{
			new MagicFieldGetterGenerator(field).generate(mw, bytecode);
		}
	}
}
