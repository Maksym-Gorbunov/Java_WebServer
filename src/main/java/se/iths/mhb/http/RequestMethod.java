package se.iths.mhb.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Implementations of this method will read a request and return a response.
 * Can be mapped to a specific Http.Method
 * Use on a method with HttpRequest as argument and HttpResponse as return value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RequestMethod {
    Http.Method value() default Http.Method.GET;
}
