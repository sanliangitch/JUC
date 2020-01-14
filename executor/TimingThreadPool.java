package executor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * 增加了日志和计时等功能的线程池
 * <p>
 * 它通过beforeExecute、afterExecute 和terminated等方法来添加日志记录和统计信息收集。
 * 为了测量任务的运行时间，beforeExecute 必须记录开始时间并把它保存到一个afterExecute可以访问的地方。
 * 因为这些方法将在执行任务的线程中调用，因此beforeExecute可以把值保存到一个ThreadLocal变量中，然后由afterExecute来读取。
 * 在TimingThreadPool中使用了两个AtomicLong变量，分别用于记录已处理的任务数和总的处理时间，
 * 并通过terminated来输出包含平均任务时间的日志消息。
 *
 * @author wulang
 * @create 2020/1/14/17:24
 */
public class TimingThreadPool extends ThreadPoolExecutor {
    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    private final Logger log = Logger.getLogger("TimingThreadPool");
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public TimingThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * 执行之前
     *
     * @param t
     * @param r
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        log.fine(String.format("Thread %s: start %s", t, r));
        startTime.set(System.nanoTime());
    }

    /**
     * 执行后
     *
     * @param r
     * @param t
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            log.fine(String.format("Thread %s: end %s, time = %dns", t, r, taskTime));
        } finally {
            super.afterExecute(r, t);
        }
    }

    /**
     * 已终止
     */
    @Override
    protected void terminated() {
        try {
            log.info(String.format("Terminated: avg time = %dns", totalTime.get() / numTasks.get()));
        } finally {
            super.terminated();
        }
    }
}
