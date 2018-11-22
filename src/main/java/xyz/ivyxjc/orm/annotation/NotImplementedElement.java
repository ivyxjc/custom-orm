package xyz.ivyxjc.orm.annotation;

/**
 * @author Ivyxjc
 * @since 11/22/2018
 */

/**
 *
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * It means that this API is not implemented, it does not work
 */
@Retention(RetentionPolicy.SOURCE)
@Target( {
             ElementType.ANNOTATION_TYPE,
             ElementType.CONSTRUCTOR,
             ElementType.FIELD,
             ElementType.METHOD,
             ElementType.PACKAGE,
             ElementType.TYPE
         })
public @interface NotImplementedElement {
}
