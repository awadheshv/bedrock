package com.citytechinc.aem.bedrock.models.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import com.citytechinc.aem.bedrock.models.impl.InheritInjector;

@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@InjectAnnotation
@Source(InheritInjector.NAME)
public @interface InheritInject {
	/**
	 * If set to true, the model can be instantiated even if there is no child
	 * resource with that name available. Default = true.
	 */
	public boolean optional() default true;
}
