import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.file.AccessFlag;
import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectInvoker;

public class Main
{
	private Class<?> clazz = Main.class;
	public static void main(String[] args)
	{
		ReflectInvoker def = ReflectInvokeFactory.getReflectInvoker(ClassLoader.class, "defineClass", false, Class.class, String.class, byte[].class, int.class, int.class);
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, "A", null, "sun/reflect/MagicAccessorImpl", null);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_STATIC, "a", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, "Main", "clazz", "Ljava/lang/Class;");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_STATIC, "b", "(Ljava/lang/Object;)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "Main$A", "c", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitEnd();
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		Class<?> c = (Class<?>) def.invoke(ClassLoader.getSystemClassLoader(), null, code, 0, code.length);
		Main m = new Main();
		ReflectInvokeFactory.getReflectInvoker(c, "a", true, void.class, Object.class, Object.class).invoke(m, new A());
		System.out.println(m.clazz.getClass());
		ReflectInvokeFactory.getReflectInvoker(c, "b", true, void.class, Object.class).invoke(new B());
	}

	public static class A
	{
		public void c()
		{
		}
	}

	public static class B
	{
		public void c()
		{
			System.out.println("CHEERS!");
		}
	}
}
