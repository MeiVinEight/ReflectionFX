package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.FieldWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class NativeDynamicBind extends DynamicBind
{
	private final ClassWriter bytecode = this.bytecode();
	private final Class<?> target = this.target();
	private final Map<String, Method> methods = new LinkedHashMap<>();
	private final Map<String, Constructor<?>> constructors = new LinkedHashMap<>();

	public NativeDynamicBind(Class<?> handle, Class<?> target)
	{
		super(handle, target);
	}

	@Override
	public void method(MethodKind implementation, MethodKind invocation, int kind)
	{
		String name = invocation.name().concat(invocation.type().toMethodDescriptorString());
		this.methods.put(name, ReflectionFactory.ACCESSOR.getMethod(this.target, invocation.name(), invocation.type().returnType(), invocation.type().parameterArray()));
		this.bytecode.addField(new FieldWriter()
			.set(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_STATIC | AccessFlag.ACC_FINAL, name, Generator.getSignature(Method.class))
		);
		new NativeDynamicBindMethodGenerator(this.target, implementation, invocation, kind).generate(this.bytecode);
	}

	@Override
	public void field(MethodKind implementation, String operation, int kind)
	{
		new NativeDynamicBindFieldGenerator(this.target, implementation, operation, kind).generate(this.bytecode);
	}

	@Override
	public void construct(MethodKind implementation, MethodKind invocation)
	{
		Constructor<?> constructor = ReflectionFactory.ACCESSOR.getConstructor(this.target, invocation.type().parameterArray());
		String name = "<init>".concat(MethodType.methodType(void.class, invocation.type().parameterArray()).toMethodDescriptorString());
		this.constructors.put(name, constructor);
		this.bytecode.addField(new FieldWriter()
			.set(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_STATIC | AccessFlag.ACC_FINAL, name, Generator.getSignature(Constructor.class))
		);
		new NativeDynamicBindConstructGenerator(this.target, implementation, invocation).generate(this.bytecode);
	}

	@Override
	public void instantiation(MethodKind implementation)
	{
		new NativeDynamicBindInstantiationGenerator(this.target, implementation).generate(this.bytecode);
	}

	@Override
	public void enumHelper()
	{
		new DynamicBindEnumHelperGenerator(this.target).generate(this.bytecode);
	}

	@Override
	public void postgenerate(Class<?> generated)
	{
		super.postgenerate(generated);
		this.methods.forEach((name, method) -> ReflectionFactory.UNSAFE.putObjectVolatile(generated, ReflectionFactory.UNSAFE.staticFieldOffset(ReflectionFactory.ACCESSOR.getField(generated, name)), method));
		this.constructors.forEach((name, method) -> ReflectionFactory.UNSAFE.putObjectVolatile(generated, ReflectionFactory.UNSAFE.staticFieldOffset(ReflectionFactory.ACCESSOR.getField(generated, name)), method));
	}
}
