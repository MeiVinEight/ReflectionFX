import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.file.AccessFlag;
import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectInvoker;

import java.io.FileOutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
		ClassWriter cw = new ClassWriter(0);
		cw.visit(
			52,
			AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER,
			"org/mve/util/reflect/ReflectInvokerImpl", "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<LMain;>;",
			"java/lang/Object",
			new String[]{"org/mve/util/reflect/ReflectInvoker"});
		MethodVisitor mv =cw.visitMethod(AccessFlag.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)LMain;", null, null);
		mv.visitCode();
		mv.visitTypeInsn(Opcodes.NEW, "Main");
		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "Main", "<init>", "()V", false);
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_BRIDGE | AccessFlag.ACC_SYNTHETIC, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/mve/util/reflect/ReflectInvokerImpl", "invoke", "([Ljava/lang/Object;)LMain;", false);
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		cw.visitSource("ReflectInvokerImpl.java", null);
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		FileOutputStream out = new FileOutputStream("ReflectInvokerImpl.class");
		out.write(code);
		out.flush();
		out.close();
		Method m = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
		m.setAccessible(true);
		Class<?> clazz = (Class<?>) m.invoke(ClassLoader.getSystemClassLoader(), null, code, 0, code.length);
		ReflectInvoker<Main> invoker = (ReflectInvoker<Main>) clazz.getDeclaredConstructor().newInstance();
		System.out.println(invoker.invoke());
		System.out.println(ReflectInvokeFactory.getReflectInvoker(Main.class, true).invoke());
//		System.out.println(ReflectInvokeFactory.constant(new Main()).invoke());
//		ClassLoader cl = new ReflectionClassLoader(ClassLoader.getSystemClassLoader());
//		Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
//		f.setAccessible(true);
//		MethodHandles.Lookup lookup = (MethodHandles.Lookup) f.get(null);
//		MethodHandle handle = lookup.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class));
//		new Main().invoke(handle, ClassLoader.getSystemClassLoader(), null, new byte[0], 0, 0);
	}

	private Object invoke(Object... args) throws Throwable
	{
		return ((MethodHandle)args[0]).asFixedArity().invokeWithArguments(Arrays.copyOfRange(args, 1, args.length));
	}
}
