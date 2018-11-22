package xyz.ivyxjc.orm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Ivyxjc
 * @since 11/22/2018
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface AuditTable {

    String name();

    String catalog() default "";

    String schema() default "";
}
