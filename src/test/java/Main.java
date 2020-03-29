import org.mve.util.reflect.ReflectInvokeFactory;

public class Main
{
	private Main()
	{
		System.out.println("CTR");
	}

	private void a(Object obj)
	{
		System.out.println("Obj");
	}

	private void a(int[] a)
	{
		System.out.println("int[]");
	}

	public static void main(String[] args) throws Exception
	{
		ReflectInvokeFactory.getReflectInvoker(Main.class, "<init>", void.class).invoke(new Main());
	}
}
