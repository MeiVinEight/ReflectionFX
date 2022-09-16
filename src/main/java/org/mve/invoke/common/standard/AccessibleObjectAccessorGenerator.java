package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.invoke.MagicAccessor;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.Unsafe;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.reflect.AccessibleObject;

public abstract class AccessibleObjectAccessorGenerator extends AccessorGenerator
{
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
			.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, JavaVM.CONSTANT[ReflectionAccessor.FIELD_OBJECTIVE], Generator.signature(AccessibleObject.class))
		);
	}

	public void postgenerate(Class<?> generated)
	{
		super.postgenerate(generated);
		Unsafe.unsafe.putObject(generated, Unsafe.unsafe.staticFieldOffset(MagicAccessor.accessor.getField(generated, JavaVM.CONSTANT[ReflectionAccessor.FIELD_OBJECTIVE])), this.accessibleObject);
	}
}
