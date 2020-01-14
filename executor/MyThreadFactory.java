package executor;

import java.util.concurrent.ThreadFactory;

/**
 * 自定义线程工厂
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
