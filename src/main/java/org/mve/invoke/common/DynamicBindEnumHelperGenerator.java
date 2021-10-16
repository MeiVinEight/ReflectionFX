package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.RuntimeVisibleAnnotationWriter;
import org.mve.asm.attribute.annotation.Annotation;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.constant.Type;
import org.mve.invoke.MagicAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

public class DynamicBindEnumHelperGenerator extends DynamicBindGenerator
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;
	private final Class<?> target = this.getTarget();

	public DynamicBindEnumHelperGenerator(Class<?> target)
	{
		super(target);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		String values;
		FIND:
		{
			Field[] fields = ReflectionFactory.ACCESSOR.getFields(getTarget());
			for (Field field : fields)
			{
				int modifier = field.getModifiers();
				if (Modifier.isPrivate(modifier) && Modifier.isStatic(modifier) && Modifier.isFinal(modifier) && field.getType().isArray() && field.getType().getComponentType() == this.target)
				{
					values = ReflectionFactory.ACCESSOR.getName(field);
					break FIND;
				}
			}
			ReflectionFactory.ACCESSOR.throwException(new NoSuchFieldException("private static final ".concat(getTarget().getName()).concat("[]")));
			return;
		}
		long offset = UNSAFE.staticFieldOffset(ACCESSOR.getField(this.target, values));
		Marker m1 = new Marker();
		bytecode
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "construct", MethodType.methodType(Object.class, String.class).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.type(Opcodes.NEW, Generator.type(target))
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ALOAD_1)
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.ARRAYLENGTH)
					.method(Opcodes.INVOKESPECIAL, Generator.type(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(4, 2)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "construct", MethodType.methodType(this.target, String.class).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.type(Opcodes.NEW, Generator.type(target))
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ALOAD_1)
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.ARRAYLENGTH)
					.method(Opcodes.INVOKESPECIAL, Generator.type(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(4, 2)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "construct", MethodType.methodType(Object.class, String.class, int.class).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.type(Opcodes.NEW, Generator.type(target))
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ILOAD_2)
					.method(Opcodes.INVOKESPECIAL, Generator.type(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(4, 3)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "construct", MethodType.methodType(this.target, String.class, int.class).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.type(Opcodes.NEW, Generator.type(target))
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ILOAD_2)
					.method(Opcodes.INVOKESPECIAL, Generator.type(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(4, 3)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "values", MethodType.methodType(Object[].class).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.ARETURN)
					.max(1, 1)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "values", "()[".concat(Generator.signature(target)))
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.ARETURN)
					.max(1, 1)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "values", MethodType.methodType(void.class, Object[].class).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKESPECIAL, bytecode.name, "clearEnumConstants", "()V", false)
					.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
					.constant(Opcodes.LDC_W, new Type(target))
					.constant(Opcodes.LDC2_W, offset)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
					.instruction(Opcodes.RETURN)
					.max(5, 2)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "values", "([".concat(Generator.signature(this.target)).concat(")V"))
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKESPECIAL, bytecode.name, "clearEnumConstants", "()V", false)
					.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
					.constant(Opcodes.LDC_W, new Type(target))
					.constant(Opcodes.LDC2_W, offset)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
					.instruction(Opcodes.RETURN)
					.max(5, 2)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "add", MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKESPECIAL, bytecode.name, "clearEnumConstants", "()V", false)
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.ASTORE_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.IADD)
					.method(Opcodes.INVOKESTATIC, Generator.type(Arrays.class), "copyOf", MethodType.methodType(Object[].class, Object[].class, int.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ASTORE_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.ISUB)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.AASTORE)
					.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
					.constant(new Type(target))
					.constant(offset)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
					.instruction(Opcodes.RETURN)
					.max(5, 3)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "add", MethodType.methodType(void.class, target).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKESPECIAL, bytecode.name, "clearEnumConstants", "()V", false)
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.ASTORE_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.IADD)
					.method(Opcodes.INVOKESTATIC, Generator.type(Arrays.class), "copyOf", MethodType.methodType(Object[].class, Object[].class, int.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ASTORE_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.ISUB)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.AASTORE)
					.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
					.constant(new Type(target))
					.constant(offset)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
					.instruction(Opcodes.RETURN)
					.max(5, 3)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "remove", MethodType.methodType(void.class, int.class).toMethodDescriptorString())
				.attribute(
					new RuntimeVisibleAnnotationWriter()
						.annotation(new Annotation().type(CONSTANT_POOL[1]))
						.annotation(new Annotation().type(CONSTANT_POOL[2]))
						.annotation(new Annotation().type(CONSTANT_POOL[3]))
				)
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKESPECIAL, bytecode.name, "clearEnumConstants", "()V", false)
					.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
					.constant(Opcodes.LDC_W, new Type(target))
					.constant(Opcodes.LDC2_W, offset)
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.ISUB)
					.type(Opcodes.ANEWARRAY, Generator.type(target))
					.instruction(Opcodes.DUP)
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.SWAP)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.SWAP)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.ILOAD_1)
					.method(Opcodes.INVOKESTATIC, Generator.type(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ILOAD_1)
					.jump(Opcodes.IF_ICMPEQ, m1)
					.instruction(Opcodes.DUP)
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.SWAP)
					.instruction(Opcodes.ILOAD_1)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.IADD)
					.instruction(Opcodes.SWAP)
					.instruction(Opcodes.ILOAD_1)
					.field(Opcodes.GETSTATIC, Generator.type(target), values, "[".concat(Generator.signature(target)))
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ILOAD_1)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.IADD)
					.instruction(Opcodes.ISUB)
					.method(Opcodes.INVOKESTATIC, Generator.type(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
					.mark(m1)
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
					.instruction(Opcodes.RETURN)
					.max(12, 2)
				)
			);

		boolean openJ9VM = UNSAFE.getJavaVMVendor().equals("Eclipse OpenJ9");

		if (openJ9VM)
		{
			bytecode.method(new MethodWriter()
				.set(AccessFlag.PRIVATE, "clearEnumConstants", "()V")
				.attribute(new CodeWriter()
					.constant(this.target)
					.instruction(Opcodes.ACONST_NULL)
					.field(Opcodes.PUTFIELD, Generator.type(Class.class), "enumVars", "Ljava/lang/Class$EnumVars;")
					.instruction(Opcodes.RETURN)
					.max(2, 1)
				)
			);
		}
		else
		{
			bytecode.method(new MethodWriter()
				.set(AccessFlag.PRIVATE, "clearEnumConstants", "()V")
				.attribute(new CodeWriter()
					.constant(this.target)
					.instruction(Opcodes.ACONST_NULL)
					.field(Opcodes.PUTFIELD, Generator.type(Class.class), "enumConstants", Generator.signature(Object[].class))
					.constant(this.target)
					.instruction(Opcodes.ACONST_NULL)
					.field(Opcodes.PUTFIELD, Generator.type(Class.class), "enumConstantDirectory", Generator.signature(Map.class))
					.instruction(Opcodes.RETURN)
					.max(2, 1)
				)
			);
		}
	}
}
