package org.mve.invoke;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MagicAccess
{
	int METHOD = 0;
	int FIELD = 1;
	int CONSTRUCT = 2;
	int INSTANTIATE = 3;

	int access();

	Class<?> objective();

	String name() default "";

	Class<?>[] type() default {};

	Class<?> value() default void.class;

	int kind() default 0;
}
