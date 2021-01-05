package co.amscraft.ultralib.editor;

import org.bukkit.Material;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Izzy on 2017-10-07.
 */
@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.TYPE)
public @interface FieldDescription {

    public String help() default "";

    public String unit() default "";

    public boolean show() default true;

    public boolean save() default true;


}

