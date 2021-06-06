package org.mve.invoke;

import org.mve.asm.ClassWriter;

public class MagicDynamicBind extends DynamicBind
{
	private final ClassWriter bytecode = this.bytecode();
	private final Class<?> target = this.target();

	public MagicDynamicBind(Class<?> handle, Class<?> target)
	{
		super(handle, target);
	}

	@Override
	public void method(MethodKind implementation, MethodKind invocation, int kind)
	{
		new MagicDynamicBindMethodGenerator(this.target, implementation, invocation, kind).generate(this.bytecode);
	}

	@Override
	public void field(MethodKind implementation, String operation, int kind)
	{
		new MagicDynamicBindFieldGenerator(this.target, implementation, operation, kind).generate(this.bytecode);
	}

	@Override
	public void construct(MethodKind implementation, MethodKind invocation)
	{
		new MagicDynamicBindConstructGenerator(this.target, implementation, invocation).generate(this.bytecode);
	}

	@Override
	public void instantiation(MethodKind implementation)
	{
		new MagicDynamicBindInstantiationGenerator(this.target, implementation).generate(this.bytecode);
	}

	@Override
	public void enumHelper()
	{
		new DynamicBindEnumHelperGenerator(this.target).generate(this.bytecode);
	}
}
