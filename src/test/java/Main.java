import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectInvoker;

public class Main
{
	public static void main(String[] args)
	{
		NullPointerException npe = new NullPointerException();
		ReflectInvoker constant = ReflectInvokeFactory.constant(npe);
		ReflectInvoker thrower = ReflectInvokeFactory.throwException();
		thrower.invoke(constant.invoke(null));
	}
}
