package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.OperandStack;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.common.Generator;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MagicMethodAccessorGenerator extends MethodAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Method method = this.getMethod();
	private final int kind = this.kind();
	private final int argument;

	public MagicMethodAccessorGenerator(Method method, int kind, Object[] argument)
	{
		super(method, kind, argument);
		this.argument = argument.length;
	}

	@Override
	public void generate()
	{
		super.generate();
		int modifiers = this.method.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		boolean	interfaces = Modifier.isAbstract(modifiers);
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.method(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.attribute(code);
		Generator.merge(code, this.bytecode.name, this.argument);
		int load = this.method.getParameterTypes().length + (statics ? 0 : 1);
		Class<?>[] parameters = this.method.getParameterTypes();
		for (int i=0; i<load; i++)
		{
			code.instruction(Opcodes.ALOAD_1)
				.number(Opcodes.BIPUSH, i)
				.instruction(Opcodes.AALOAD);
			Class<?> parameterType;
			if ((statics || i > 0) && (parameterType = parameters[statics ? i : (i-1)]).isPrimitive())
			{
				unwarp(parameterType, code);
			}
		}
		code.method(this.kind + 0xB6, Generator.type(this.method.getDeclaringClass()), ReflectionFactory.ACCESSOR.getName(this.method), MethodType.methodType(this.method.getReturnType(), this.method.getParameterTypes()).toMethodDescriptorString(), interfaces);
		if (method.getReturnType() == void.class)
		{
			code.instruction(Opcodes.ACONST_NULL);
		}
		else
		{
			Generator.warp(method.getReturnType(), code);
		}
		code.instruction(Opcodes.ARETURN)
			.max(Math.max(this.stack(), 5), 3);
		if (statics && parameters.length == 0)
		{
			mw = new MethodWriter().set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class).toMethodDescriptorString());
			this.bytecode.method(mw);
			Generator.inline(mw);
			code = new CodeWriter();
			mw.attribute(code);
			code.method(this.kind + 0xB6, Generator.type(this.method.getDeclaringClass()), ReflectionFactory.ACCESSOR.getName(this.method), MethodType.methodType(this.method.getReturnType()).toMethodDescriptorString(), interfaces);
			if (method.getReturnType() == void.class)
			{
				code.instruction(Opcodes.ACONST_NULL);
			}
			else
			{
				Generator.warp(method.getReturnType(), code);
			}
			int ts = Generator.typeSize(this.method.getReturnType());
			code.instruction(Opcodes.ARETURN)
				.max(ts == 0 ? 1 : ts, 1);
		}
	}

	private int stack()
	{
		OperandStack stack = new OperandStack();
		int modifiers = this.method.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		if (!statics)
		{
			stack.push();
		}
		Class<?>[] parameters = this.method.getParameterTypes();
		for (Class<?> c : parameters)
		{
			stack.push();
			if (c == double.class || c == long.class)
			{
				stack.push();
			}
		}
		for (Class<?> c : parameters)
		{
			stack.pop();
			if (c == double.class || c == long.class)
			{
				stack.pop();
			}
		}
		if (!statics)
		{
			stack.pop();
		}
		stack.push();
		if (this.method.getReturnType() == long.class || this.method.getReturnType() == double.class)
		{
			stack.push();
		}
		stack.pop();
		if (this.method.getReturnType() == long.class || this.method.getReturnType() == double.class)
		{
			stack.pop();
		}
		return stack.getMaxSize();
	}
}
