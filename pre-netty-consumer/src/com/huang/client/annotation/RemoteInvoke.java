//标记需要特殊处理的字段，具体在项目中用于实现远程调用代理。
//标记类中的某些字段，表明这些字段需要通过动态代理机制赋值为代理对象，而不是普通的直接实例化。
package com.huang.client.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})//限定了 @RemoteInvoke 只能用于类的字段（FIELD）。不能用于方法、参数等其他地方。
@Retention(RetentionPolicy.RUNTIME)//指定 @RemoteInvoke 注解的生命周期为 RUNTIME
@Documented
public @interface RemoteInvoke {//定义了一个空的自定义注解 RemoteInvoke，无参数。用于标记需要动态代理处理的字段。

}
