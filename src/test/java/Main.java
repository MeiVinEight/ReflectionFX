import org.mve.util.reflect.ReflectInvokeFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Main
{
	private final byte v1;
	private final short v2;
	private final int v3;
	private final long v4;
	private final float v5;
	private final double v6;
	private final boolean v7;
	private final char v8;
	private final Object v9;

	public Main(byte v1, short v2, int v3, long v4, float v5, double v6, boolean v7, char v8, Object v9)
	{
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
		this.v5 = v5;
		this.v6 = v6;
		this.v7 = v7;
		this.v8 = v8;
		this.v9 = v9;
	}

	public static void main(String[] args) throws Exception
	{
		Main m = new Main((byte)1, (short)1, 1, 1, 1, 1, false, '1', new Object());
		System.out.println(m.v6);
		ReflectInvokeFactory.getReflectInvoker(Main.class, "v6", true).invoke(m, 2);
		System.out.println(m.v6);
	}
}
