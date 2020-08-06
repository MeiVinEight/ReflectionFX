package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.file.AccessFlag;

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
		MethodWriter mw = bytecode.addMethod(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		Generator.inline(mw);
		if (this.kind() == ReflectionFactory.KIND_PUT)
		{
			new UnsafeFieldSetterGenerator(field).generate(mw);
		}
		else
		{
			new UnsafeFieldGetterGenerator(field).generate(mw);
		}
	}
}
