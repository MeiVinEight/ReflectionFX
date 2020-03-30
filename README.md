# Toolkits
用来简化反射的工具
# 反射调用方法
  ReflectInvoker invoker = ReflectInvokeFactory.getReflectInvoker(类, 方法名, 返回值类型, 形参类型列表);
  
  Object returnValue = invoker.invoke(obj, 参数列表);
  
  静态方法第一个参数可以为null，非静态方法第一个参数为调用的对象
  
  ReflectInvoker invoker = ReflectInvokeFactory(类加载器, 类名, 方法名, 返回值类型, 形参类型列表);
  
  这个方法可以用来反射不可以直接使用的类或特殊类比如BootstrapClassLoader加载的类
# 反射属性
  ReflectInvoker invoker = ReflectInvokeFactory.getReflectInvoker(类, 属性名);

  Object fieldValue = invoker.invoke(null);

  invoker.invoke(null, obj);

  静态属性第一个参数可以为null，非静态属性第一个参数是要反射的对象
  第二个参数不为空的时候会把传入的值赋值给指定的属性
  

  ReflectInvoker invoker = ReflectInvokeFactory.getReflectInvoker(类加载器, 类名, 属性名);
  
  这个方法可以用来反射不可以直接使用的类或特殊类比如BootstrapClassLoader加载的类


  反射赋值final属性需要指定深度反射
  
  ReflectInvoker invoker = ReflectInvokeFactory.getReflectInvoker(类, 属性名， true);
  
  ReflectInvoker invoker = ReflectInvokeFactory.getReflectInvoker(类加载器, 类名, 属性名, true);
  
  最后一个参数默认为false表示非深度反射，修改final属性会抛出IllegalOperationException
