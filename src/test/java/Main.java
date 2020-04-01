import org.mve.util.reflect.ReflectInvokeFactory;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println(ReflectInvokeFactory.getReflectInvoker(ClassLoader.getSystemClassLoader(), "jdk.internal.reflect.DelegatingClassLoader", ClassLoader.class).invoke(ClassLoader.getSystemClassLoader()));
	}
}
