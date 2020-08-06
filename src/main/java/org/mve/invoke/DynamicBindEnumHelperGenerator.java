package org.mve.invoke;

import org.mve.util.asm.AnnotationWriter;
import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.Marker;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.Type;
import org.mve.util.asm.attribute.RuntimeVisibleAnnotationsWriter;
import org.mve.util.asm.file.AccessFlag;

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
					values = field.getName();
					break FIND;
				}
			}
			ReflectionFactory.ACCESSOR.throwException(new NoSuchFieldException("private static final ".concat(getTarget().getName()).concat("[]")));
			return;
		}
		long offset = UNSAFE.staticFieldOffset(ACCESSOR.getField(this.target, values));
		Marker m1 = new Marker();
		bytecode.addMethod(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(Object.class, String.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, Generator.getType(target))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ALOAD_1)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(4, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(this.target, String.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, Generator.getType(target))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ALOAD_1)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(4, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(Object.class, String.class, int.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, Generator.getType(target))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.ILOAD_2)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(4, 3)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(this.target, String.class, int.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, Generator.getType(target))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.ILOAD_2)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(4, 3)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "values", MethodType.methodType(Object[].class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "values", "()[".concat(Generator.getSignature(target)))
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "values", MethodType.methodType(void.class, Object[].class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addInstruction(Opcodes.ALOAD_0)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, bytecode.getName(), "clearEnumConstants", "()V", false)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(target))
			.addConstantInstruction(Opcodes.LDC2_W, offset)
			.addInstruction(Opcodes.ALOAD_1)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(5, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "values", "([".concat(Generator.getSignature(this.target)).concat(")V"))
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addInstruction(Opcodes.ALOAD_0)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, bytecode.getName(), "clearEnumConstants", "()V", false)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(target))
			.addConstantInstruction(Opcodes.LDC2_W, offset)
			.addInstruction(Opcodes.ALOAD_1)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(5, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "add", MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addInstruction(Opcodes.ALOAD_0)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, bytecode.getName(), "clearEnumConstants", "()V", false)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.ASTORE_2)
			.addInstruction(Opcodes.ALOAD_2)
			.addInstruction(Opcodes.ALOAD_2)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.IADD)
			.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(Arrays.class), "copyOf", MethodType.methodType(Object[].class, Object[].class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ASTORE_2)
			.addInstruction(Opcodes.ALOAD_2)
			.addInstruction(Opcodes.ALOAD_2)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.ISUB)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.AASTORE)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addConstantInstruction(new Type(target))
			.addConstantInstruction(offset)
			.addInstruction(Opcodes.ALOAD_2)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(5, 3)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "add", MethodType.methodType(void.class, target).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addInstruction(Opcodes.ALOAD_0)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, bytecode.getName(), "clearEnumConstants", "()V", false)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.ASTORE_2)
			.addInstruction(Opcodes.ALOAD_2)
			.addInstruction(Opcodes.ALOAD_2)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.IADD)
			.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(Arrays.class), "copyOf", MethodType.methodType(Object[].class, Object[].class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ASTORE_2)
			.addInstruction(Opcodes.ALOAD_2)
			.addInstruction(Opcodes.ALOAD_2)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.ISUB)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.AASTORE)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addConstantInstruction(new Type(target))
			.addConstantInstruction(offset)
			.addInstruction(Opcodes.ALOAD_2)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(5, 3)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "remove", MethodType.methodType(void.class, int.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
			)
			.addCode()
			.addInstruction(Opcodes.ALOAD_0)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, bytecode.getName(), "clearEnumConstants", "()V", false)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(target))
			.addConstantInstruction(Opcodes.LDC2_W, offset)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.ISUB)
			.addTypeInstruction(Opcodes.ANEWARRAY, Generator.getType(target))
			.addInstruction(Opcodes.DUP)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ICONST_0)
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ICONST_0)
			.addInstruction(Opcodes.ILOAD_1)
			.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ILOAD_1)
			.addJumpInstruction(Opcodes.IF_ICMPEQ, m1)
			.addInstruction(Opcodes.DUP)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ILOAD_1)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.IADD)
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ILOAD_1)
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(target), values, "[".concat(Generator.getSignature(target)))
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ILOAD_1)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.IADD)
			.addInstruction(Opcodes.ISUB)
			.addMethodInstruction(Opcodes.INVOKESTATIC, Generator.getType(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
			.mark(m1)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(12, 2);

		boolean openJ9VM = UNSAFE.getJavaVMVendor().equals("Eclipse OpenJ9");

		if (openJ9VM)
		{
			bytecode.addMethod(AccessFlag.ACC_PRIVATE, "clearEnumConstants", "()V")
				.addCode()
				.addConstantInstruction(this.target)
				.addInstruction(Opcodes.ACONST_NULL)
				.addFieldInstruction(Opcodes.PUTFIELD, Generator.getType(Class.class), "enumVars", "Ljava/lang/Class$EnumVars;")
				.addInstruction(Opcodes.RETURN)
				.setMaxs(2, 1);
		}
		else
		{
			bytecode.addMethod(AccessFlag.ACC_PRIVATE, "clearEnumConstants", "()V")
				.addCode()
				.addConstantInstruction(this.target)
				.addInstruction(Opcodes.ACONST_NULL)
				.addFieldInstruction(Opcodes.PUTFIELD, Generator.getType(Class.class), "enumConstants", Generator.getSignature(Object[].class))
				.addConstantInstruction(this.target)
				.addInstruction(Opcodes.ACONST_NULL)
				.addFieldInstruction(Opcodes.PUTFIELD, Generator.getType(Class.class), "enumConstantDirectory", Generator.getSignature(Map.class))
				.addInstruction(Opcodes.RETURN)
				.setMaxs(2, 1);
		}
	}
}
