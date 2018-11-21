package xyz.ivyxjc.orm.enumerations;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Ivyxjc
 * @since 11/18/2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SupportedTypes {

    public static final String javaLangInteger = "java.lang.Integer";
    public static final String javaLangLong = "java.lang.Long";
    public static final String javaLangDouble = "java.lang.Double";
    public static final String javaLangFloat = "java.lang.Float";
    public static final String javaLangBoolean = "java.lang.Boolean";


    public static final String javaLangString = "java.lang.String";
    public static final String javaUtilDate = "java.Util.Date";
    public static final String javaSqlDate = "java.sql.Date";
    public static final String javaSqlTimestamp = "java.sql.Timestamp";
    public static final String javaTimeLocalDateTime = "java.time.LocalDateTime";
    public static final String javaTimeLocalDate = "java.time.LocalDate";
}
