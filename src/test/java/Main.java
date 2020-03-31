import kotlin.KotlinNullPointerException;
import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.ReflectInvoker;

public class Main
{
	private int i;
	public static void main(String[] args)
	{
		ReflectInvoker invoker = ReflectInvokeFactory.getReflectInvoker(Main.class, "a", void.class, int.class, double.class, NullPointerException.class);
		Main m = new Main();
		System.out.println(m.i);
		invoker.invoke(m, 2, 3.5, new KotlinNullPointerException());
		System.out.println(m.i);
	}

	private void a(int i, double d, NullPointerException o)
	{
		this.i = (int) (d*i);
		o.printStackTrace(System.out);
	}
}
