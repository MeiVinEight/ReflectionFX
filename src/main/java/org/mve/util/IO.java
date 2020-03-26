package org.mve.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IO
{
	public static byte[] toByteArray(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
		int len;
		byte[] b = new byte[1024];
		while ((len = in.read(b)) > -1) out.write(b, 0, len);
		return out.toByteArray();
	}
}
