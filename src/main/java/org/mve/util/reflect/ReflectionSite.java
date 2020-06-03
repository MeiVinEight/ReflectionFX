package org.mve.util.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReflectionSite
{
	int
	REFLECTION_TYPE_METHOD			= 0,
	REFLECTION_TYPE_FIELD			= 1,
	REFLECTION_TYPE_NEW				= 2,
	REFLECTION_TYPE_NEW_CONSTRUCT	= 3;

	int
	KIND_INVOKE_STATIC		= 0,
	KIND_INVOKE_SPECIAL		= 1,
	KIND_INVOKE_INTERFACE	= 2,
	KIND_INVOKE_VIRTUAL		= 3,
	KIND_GET_STATIC			= 4,
	KIND_GET_FIELD			= 5,
	KIND_PUT_STATIC			= 6,
	KIND_PUT_FIELD			= 7;

	int type();
	int kind();
}
