package com.citytechinc.aem.bedrock.core.components.multicompositefield;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface MultiCompositeField {

    boolean matchBaseName() default true;

    String prefix() default "./";
}