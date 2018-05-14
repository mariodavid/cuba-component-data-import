package de.diedavids.cuba.dataimport.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithImport {
    String listComponent() default "";
    String buttonId() default "importBtn";
    String buttonsPanel() default "buttonsPanel";
}