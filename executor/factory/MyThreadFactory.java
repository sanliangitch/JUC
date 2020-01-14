package executor.factory;

import java.util.concurrent.ThreadFactory;

/**
 * MyThreadFactory中给出了一个自定义的线程工厂。
 * <p>
 * 它创建了一个新的MyAppThread实例，并将一个特定于线程池的名字传递给MyAppThread的构造函数，
 * 从而可以在线程转储和错误日志信息中区分来自不同线程池的线程。
 * 在应用程序的其他地方也可以使用MyAppThread,以便所有线程都能使用它的调试功能。
 *
 * @author wulang
 * @create 2020/1/14/16:52
 */
public class MyThreadFactory implements ThreadFactory {
    private final String poolName;

    public MyThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new MyAppThread(runnable, poolName);
    }
}
