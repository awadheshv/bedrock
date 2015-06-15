package com.citytechinc.aem.bedrock.models.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import com.citytechinc.aem.bedrock.models.impl.ImageInjector;

@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@InjectAnnotation
@Source(ImageInjector.NAME)
public @interface ImageInject {
	/**
	 * If set to true, the model can be instantiated even if there is no child
	 * resource with that name available. Default = true.
	 */
	public boolean optional() default true;

	/**
	 * The path to the image from the current resource. If none is set it will
	 * use the current resource.
	 */

	public String path() default "";

	/**
	 * Whether to get the link via inheriting
	 */

	public boolean inherit() default false;
}
