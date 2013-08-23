package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation can be used to set the size of the thread pool used in a 
 * multiactive object.
 * 
 * @author The ProActive Team
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface DefineThreadLimits {

	/** The size of the thread pool */
	public int threadPoolSize() default Integer.MAX_VALUE;

}
