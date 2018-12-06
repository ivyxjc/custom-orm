package xyz.ivyxjc.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivyxjc
 * @since 11/22/2018
 */
@Repeatable(AuditColumns.class)
@Target( {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditColumn {

    String name();

    String defaultValue() default "";

    @NotImplementedElement
    String dbType() default "";

    @NotImplementedElement
    Class javaType() default Object.class;
}
