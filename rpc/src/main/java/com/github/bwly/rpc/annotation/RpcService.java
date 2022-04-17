package com.github.bwly.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author wl
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {
    String group() default "";

    String version() default "";
}
