public class Main
{
	private NullPointerException npe = new NullPointerException();
	public static void main(String[] args) throws Exception
	{
//		Main m = new Main();
//		System.out.println(m.npe.getClass());
//		{
//			ClassWriter cw = new ClassWriter(0);
//			cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, "A", null, "sun/reflect/MagicAccessorImpl", null);
//			MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_STATIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
//			mv.visitCode();
//			mv.visitVarInsn(Opcodes.ALOAD, 0);
//			mv.visitVarInsn(Opcodes.ALOAD, 1);
//			mv.visitFieldInsn(Opcodes.PUTFIELD, "Main", "npe", "Ljava/lang/NullPointerException;");
//			mv.visitInsn(Opcodes.RETURN);
//			mv.visitMaxs(2, 2);
//			mv.visitEnd();
//			cw.visitEnd();
//			byte[] code = cw.toByteArray();
//			ReflectInvoker definer = ReflectInvokeFactory.getReflectInvoker(ClassLoader.class, "defineClass", false, Class.class, String.class, byte[].class, int.class, int.class);
//			Class<?> clazz = (Class<?>) definer.invoke(Main.class.getClassLoader(), null, code, 0 , code.length);
//			ReflectInvokeFactory.getReflectInvoker(clazz, "set", true, void.class, Object.class, Object.class).invoke(m, new Object());
//		}
//		System.out.println(m.npe.getClass());
	}
}
