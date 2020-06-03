package org.mve;

import org.mve.util.reflect.ReflectionSite;

public interface FP
{
	@ReflectionSite(
		type = ReflectionSite.REFLECTION_TYPE_NEW,
		kind = ReflectionSite.KIND_INVOKE_SPECIAL
	)
	Main call();
}
