package zqmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author zhouqing
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 * @// TODO: 2020/8/12 该注解可以使用在类和方法上，只有使用了@Router的类，zqMVC才会扫描到该注解。
 */

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface URLMapping {

    String value() default "";
}
