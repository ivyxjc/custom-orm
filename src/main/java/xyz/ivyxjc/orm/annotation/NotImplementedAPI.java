package xyz.ivyxjc.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivyxjc
 * @since 11/21/2018
 */

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
public @interface NotImplementedAPI {
}
