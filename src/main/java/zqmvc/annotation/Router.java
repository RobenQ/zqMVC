package zqmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhouqing
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 * @// TODO: 2020/8/12 此注解只能用于类上，使用该注解告诉zqMVC被注解的类是一个路由，zqMVC启动的时候便会扫描到该类。
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Router {

}
