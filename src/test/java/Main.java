import org.mve.util.reflect.ReflectInvokeFactory;

public class Main
{
	private final int a;
	private final double b;
	private Main(int a, double b)
	{
		this.a = a;
		this.b = b;
	}
	public static void main(String[] args) throws Exception
	{
		Main m = (Main) ReflectInvokeFactory.getReflectInvoker(Main.class, false, int.class, double.class).invoke(null);
		System.out.println(m.a);
		System.out.println(m.b);
		ReflectInvokeFactory.getReflectInvoker(Main.class, "<init>", void.class, int.class, double.class).invoke(m, 1, 2);
		System.out.println(m.a);
		System.out.println(m.b);
	}
}
