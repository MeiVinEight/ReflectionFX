package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.invoke.MagicAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.reflect.AccessibleObject;

public abstract class AccessibleObjectAccessorGenerator extends AccessorGenerator
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;
	private final AccessibleObject accessibleObject;

	public AccessibleObjectAccessorGenerator(AccessibleObject accessibleObject, Class<?> target, Object[] argument)
	{
		super(target, argument);
		this.accessibleObject = accessibleObject;
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.field(new FieldWriter()
			.set(AccessFlag.PRIVATE | AccessFlag.STATIC | AccessFlag.FINAL, Generator.CONSTANT_POOL[5], Generator.signature(AccessibleObject.class))
		);
	}

	public void postgenerate(Class<?> generated)
	{
		super.postgenerate(generated);
		UNSAFE.putObjectVolatile(generated, UNSAFE.staticFieldOffset(ACCESSOR.getField(generated, Generator.CONSTANT_POOL[5])), this.accessibleObject);
	}
}
