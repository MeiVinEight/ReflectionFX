package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.reflect.AccessibleObject;

public abstract class AccessibleObjectAccessorGenerator extends AccessorGenerator
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;
	private final ClassWriter bytecode = this.bytecode();
	private final AccessibleObject accessibleObject;

	public AccessibleObjectAccessorGenerator(AccessibleObject accessibleObject, Class<?> target)
	{
		super(target);
		this.accessibleObject = accessibleObject;
	}

	public void pregenerate()
	{
		super.pregenerate();
		this.bytecode.addField(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_STATIC | AccessFlag.ACC_FINAL, "1", Generator.getSignature(AccessibleObject.class));
	}

	public void postgenerate(Class<?> generated)
	{
		super.postgenerate(generated);
		UNSAFE.putObjectVolatile(generated, UNSAFE.staticFieldOffset(ACCESSOR.getField(generated, "1")), this.accessibleObject);
	}
}
