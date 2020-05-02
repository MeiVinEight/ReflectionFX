package org.mve;

import org.mve.test.Test;
import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.Marker;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandle;

public class Main
{
	private static NullPointerException npe;

	public static void main(String[] args) throws Throwable
	{
		Marker m1 = new Marker();
		byte[] classcode = new ClassWriter()
			.set(0x34, 0x21, "A", "java/lang/Object", new String[]{"java/lang/Runnable"})
			.addMethod(new MethodWriter()
				.set(0x01, "run", "()V")
				.addAttribute(new CodeWriter()
					.addFieldInstruction(Opcodes.GETSTATIC, "org/mve/Main", "npe", "Ljava/lang/NullPointerException;")
					.addInstruction(Opcodes.DUP)
					.addJumpInstruction(Opcodes.IFNULL, m1)
					.addInstruction(Opcodes.ATHROW)
					.mark(m1)
					.addInstruction(Opcodes.POP)
					.addFieldInstruction(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
					.addInstruction(Opcodes.ACONST_NULL)
					.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false)
					.setMaxs(2, 1)
				)
			).toByteArray();

		Runnable r = (Runnable) ReflectionFactory.UNSAFE.allocateInstance(ReflectionFactory.UNSAFE.defineAnonymousClass(Main.class, classcode, null));
		r.run();
	}

	private static void a() throws Throwable
	{
		ReflectionFactory.getReflectionAccessor(Test.class, "method", true, false, false, void.class).invoke();
		MethodHandle handle = Test.get();
		handle.invoke();
		handle.invoke();
	}

	static
	{
		System.out.println("<clinit>");
	}
}
