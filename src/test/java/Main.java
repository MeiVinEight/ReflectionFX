import org.mve.util.SystemUtil;
import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectInvoker;

public class Main
{
	private final int a;
	public Main(int a)
	{
		this.a = a;
	}

	public static void main(String[] args)
	{
		try
		{
			Main m = new Main(0);
			System.out.println(m.a);
			ReflectInvoker invoker =
				ReflectInvokeFactory.getReflectInvoker(
					Main.class.getClassLoader(),
					"Main",
					"<init>",
					void.class, int.class);
			invoker.invoke(m, 1);
			System.out.println(m.a);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void a()
	{
		SystemUtil.printStackTrace();
	}
}
