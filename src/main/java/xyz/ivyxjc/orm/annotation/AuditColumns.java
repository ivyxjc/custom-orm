package xyz.ivyxjc.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivyxjc
 * @since 11/22/2018
 */
@Target( {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditColumns {
    AuditColumn[] value();
}
