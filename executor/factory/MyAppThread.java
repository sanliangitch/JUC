package executor.factory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 定制 Thread 基类
 *
 * @author wulang
 * @create 2020/1/14/16:54
 */
public class MyAppThread extends Thread {
    public static final String DEFAULT_NAME = "MyAppThread";
    private static volatile boolean debugLifecycle = false;
    private static final AtomicInteger created = new AtomicInteger();
    private static final AtomicInteger alive = new AtomicInteger();
    private static final Logger log = Logger.getAnonymousLogger();

    public MyAppThread(Runnable r) {
        this(r, DEFAULT_NAME);
    }

    public MyAppThread(Runnable runnable, String name) {
        super(runnable, name + "-" + created.incrementAndGet());
        //设置未捕获的异常处理程序
        setUncaughtExceptionHandler((t, e) -> {
            log.log(Level.SEVERE,"UNCAUGHT in thread" + t.getName(),e);
        });
    }

    @Override
    public void run() {
        //复制debug标志以确保一致的值
        boolean debug = debugLifecycle;
        if (debug) {
            log.log(Level.FINE, "created" + getName());
        }
        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();
            if (debug) {
                log.log(Level.FINE, "Exiting" + getName());
            }
        }
    }

    /**
     * 获取创建的线程
     * @return
     */
    public static int getThreadsCreated(){return created.get();}

    /**
     * 保持线程活跃
     * @return
     */
    public static int getThreadsAlive(){return alive.get();}
    public static boolean getDebug(){return debugLifecycle;}
    public static void setDebug(boolean b){debugLifecycle = b;}
}
