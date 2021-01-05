package co.amscraft.ultralib.utils.savevar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The field must be static
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SaveVar {
    public String file() default "config.yml";
}

