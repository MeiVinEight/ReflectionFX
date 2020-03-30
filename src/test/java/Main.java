import org.mve.util.reflect.ReflectInvokeFactory;

public class Main
{
	public static final int a=1;
	public static void main(String[] args) throws Exception
	{
		System.out.println(Main.a);
		System.out.println(ReflectInvokeFactory.getReflectInvoker(Main.class, "a", true).invoke(null, 2));
	}
}
