package org.mve.invoke.common.polymorphism;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.invoke.MagicAccessor;
import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.standard.UnsafeFieldGetterGenerator;
import org.mve.invoke.common.standard.UnsafeFieldSetterGenerator;

import java.lang.reflect.Field;

public class NativePolymorphismFieldGenerator extends PolymorphismFieldGenerator
{
	public NativePolymorphismFieldGenerator(Class<?> target, MethodKind implementation, String operation, int kind)
	{
		super(target, implementation, operation, kind);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		Field field = MagicAccessor.accessor.getField(this.getTarget(), this.operation());
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		bytecode.method(mw);
		Generator.inline(mw);
		if (this.kind() == ReflectionFactory.KIND_PUT)
		{
			new UnsafeFieldSetterGenerator(field).generate(mw, bytecode);
		}
		else if (this.kind() == ReflectionFactory.KIND_GET)
		{
			new UnsafeFieldGetterGenerator(field).generate(mw, bytecode);
		}
	}
}
