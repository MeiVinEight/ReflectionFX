public class Main
{
	public static void main(String[] args)
	{
		try
		{
			Object obj = new Object();
			throw new ObjectThrowable(obj);
		}
		catch (ObjectThrowable o)
		{
			Object obj = o.obj;
		}
	}

	public static class ObjectThrowable extends Throwable
	{
		private final Object obj;

		public ObjectThrowable(Object obj)
		{
			this.obj = obj;
		}
	}
}
