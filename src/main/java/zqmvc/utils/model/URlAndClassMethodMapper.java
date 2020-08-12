package zqmvc.utils.model;

import java.lang.reflect.Method;

/**
 * @author zhouqing
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 * @// TODO: 2020/8/12 该类用于保存URL的映射处理
 */

public class URlAndClassMethodMapper {

    private Object object;
    private Method method;

    public URlAndClassMethodMapper(Object object,Method method){
        this.object = object;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public Method getMethod() {
        return method;
    }
}
