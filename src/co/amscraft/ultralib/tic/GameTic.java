package co.amscraft.ultralib.tic;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.events.ServerTickEvent;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-08-21.
 */
public class GameTic {
    private static HashMap<Method, Long> methods = new HashMap<>();
    private static boolean running = false;


    private GameTic() {
    }


    public static void register(Method method) {
        if (method.getAnnotationsByType(ServerTic.class).length > 0 && !methods.containsKey(method) && method.getParameterCount() == 0) {
            methods.put(method, System.currentTimeMillis());
        }
    }

    public static void register(Class<?> type) {
        for (Method method : type.getMethods()) {
            register(method);
        }
    }

    public static void start() {
        if (!running) {
            tic();
        }
        running = true;
    }


    private static void tic() {
        try {
            new ServerTickEvent().dispatch();
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
        for (Method method : methods.keySet()) {
            try {
                ServerTic tic = method.getAnnotation(ServerTic.class);
                if (System.currentTimeMillis() - methods.get(method) > tic.delay() * 1000) {
                    methods.put(method, System.currentTimeMillis());
                    BukkitRunnable runnable = new BukkitRunnable() {
                        public void run() {
                            try {
                                method.setAccessible(true);
                                method.invoke(null);
                            } catch (Throwable e) {
                                ObjectUtils.debug(Level.WARNING, "Error while invoking method: " + method.getName() + " of class " + method.getDeclaringClass().getName());
                                e.printStackTrace();
                            }
                        }
                    };
                    if (tic.isAsync()) {
                        runnable.runTaskAsynchronously(UltraLib.getInstance());
                    } else {
                        runnable.runTask(UltraLib.getInstance());
                    }
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }
        new BukkitRunnable() {
            public void run() {
                try {
                    tic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(UltraLib.getInstance(), 1L);
    }
}
