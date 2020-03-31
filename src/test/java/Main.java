import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectInvoker;

public class Main
{
	private int i;
	public static void main(String[] args)
	{
		ReflectInvoker invoker = ReflectInvokeFactory.getReflectInvoker(Main.class, "i");
	}
}
