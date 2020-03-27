import org.mve.util.asm.ClassFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Main
{
	public static void main(String[] args)
	{
//		try
//		{
//			throw new IOException();
//		}
//		catch (@U(2) IOException e)
//		{
//			e.printStackTrace();
//		}
		try
		{
			FileInputStream in = new FileInputStream("MinecraftServer.class");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len;
			while ((len = in.read(b)) > -1) out.write(b, 0, len);
			ClassFile file = new ClassFile(out.toByteArray());
			System.out.println(file.getConstantPool().getConstantPoolSize());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE_USE, ElementType.LOCAL_VARIABLE})
	public @interface U
	{
		int value() default 1;
	}
}
