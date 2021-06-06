package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.MagicAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.reflect.AccessibleObject;

public abstract class AccessibleObjectAccessorGenerator extends AccessorGenerator
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;
	private final AccessibleObject accessibleObject;

	public AccessibleObjectAccessorGenerator(AccessibleObject accessibleObject, Class<?> target)
	{
		super(target);
		this.accessibleObject = accessibleObject;
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.addField(new FieldWriter()
			.set(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_STATIC | AccessFlag.ACC_FINAL, "1", Generator.getSignature(AccessibleObject.class))
		);
	}

	public void postgenerate(Class<?> generated)
	{
		super.postgenerate(generated);
		UNSAFE.putObjectVolatile(generated, UNSAFE.staticFieldOffset(ACCESSOR.getField(generated, "1")), this.accessibleObject);
	}
}
