import org.mve.util.asm.ClassFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			File file = new File("1.14.4");
			File[] classes = file.listFiles();
			if (classes == null) return;
			for (File f : classes)
			{
				FileInputStream in = new FileInputStream(f);
				ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
				byte[] b = new byte[1024];
				int len;
				while ((len = in.read(b)) > -1) out.write(b, 0, len);
				out.flush();
				out.close();
				in.close();
				ClassFile clazz = new ClassFile(out.toByteArray());
				System.out.println(clazz);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
