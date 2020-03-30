import org.mve.util.reflect.ReflectInvokeFactory;

public class Main
{
	private int a;

	private Main(int a)
	{
		this.a = a;
	}

	public static void main(String[] args) throws Exception
	{
		Main m = new Main(1);
		System.out.println(m.a);
		System.out.println(ReflectInvokeFactory.getReflectInvoker(Main.class, "a").invoke(m, 2));
		System.out.println(m.a);
	}
}
