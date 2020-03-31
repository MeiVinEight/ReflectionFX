import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectInvoker;

public class Main
{
	public static void main(String[] args)
	{
		ReflectInvoker thrower = ReflectInvokeFactory.throwException();
		thrower.invoke(new Exception());
	}
}
