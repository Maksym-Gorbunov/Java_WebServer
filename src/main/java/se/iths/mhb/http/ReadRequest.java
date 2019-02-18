package se.iths.mhb.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Implementations of this method will read ALL requests atm
 * Use on a void method with HttpRequest as argument
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReadRequest {
    String[] value() default {"*"};
}
