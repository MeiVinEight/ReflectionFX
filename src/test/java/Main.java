import org.mve.util.SystemUtil;
import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectionGenericException;

public class Main
{
	public static void main(String[] args) throws ReflectionGenericException, NoSuchMethodException
	{
		ReflectInvokeFactory.getReflectInvoker(Main.class, "a", void.class).invoke(null);
	}

	private static void a()
	{
		SystemUtil.printStackTrace();
	}
}
