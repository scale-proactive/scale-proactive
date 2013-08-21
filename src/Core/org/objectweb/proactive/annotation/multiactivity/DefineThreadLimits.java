package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface DefineThreadLimits {

	public int threadPoolSize() default Integer.MAX_VALUE;

}
