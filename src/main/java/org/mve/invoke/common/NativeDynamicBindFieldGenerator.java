package org.mve.invoke;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.file.AccessFlag;

import java.lang.reflect.Field;

public class NativeDynamicBindFieldGenerator extends DynamicBindFieldGenerator
{
	public NativeDynamicBindFieldGenerator(Class<?> target, MethodKind implementation, String operation, int kind)
	{
		super(target, implementation, operation, kind);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		Field field = ReflectionFactory.ACCESSOR.getField(this.getTarget(), this.operation());
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		bytecode.addMethod(mw);
		Generator.inline(mw);
		if (this.kind() == ReflectionFactory.KIND_PUT)
		{
			new UnsafeFieldSetterGenerator(field).generate(mw, bytecode);
		}
		else
		{
			new UnsafeFieldGetterGenerator(field).generate(mw, bytecode);
		}
	}
}
