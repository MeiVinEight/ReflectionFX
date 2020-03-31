import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.file.AccessFlag;
import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectionGenericException;

public class Main
{
	public static void main(String[] args) throws ReflectionGenericException
	{
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, "A", null, "java/lang/Object", null);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("HelloWorld");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		Class<?> clazz = (Class<?>) ReflectInvokeFactory.getReflectInvoker(
			ClassLoader.class,
			"defineClass",
			false,
			Class.class,
			String.class,
			byte[].class,
			int.class,
			int.class
		).invoke(ClassLoader.getSystemClassLoader(), null, code, 0, code.length);
		ReflectInvokeFactory.getReflectInvoker(clazz, "main", true, void.class, String[].class).invoke(null, (Object) null);
	}
}
