package co.amscraft.ultralib.tic;

/**
 * Created by Izzy on 2017-10-24.
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ServerTic {
    public boolean isAsync() default false;

    public double delay() default 0.05;
}
