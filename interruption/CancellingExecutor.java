package interruption;

import java.util.concurrent.*;

/**
 * 我们可以通过newTaskFor方法来进一步优化ReaderThread中封装非标准取消的技术，
 * 这是Java 6在ThreadPoolExecutor中的新增功能。当把一个Callable提交给ExecutorService 时，
 * submit方法会返回一个Future，我们可以通过这个Future来取消任务。newTaskFor 是一个工厂方法，
 * 它将创建Future来代表任务。
 * newTaskFor 还能返回一个RunnableFuture接口，该接口扩展了Future和Runnable (并由FutureTask实现)。
 *
 * @author wulang
 * @create 2020/1/13/22:00
 */
public class CancellingExecutor extends ThreadPoolExecutor {
    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof CancellableTask) {
            return ((CancellableTask<T>) callable).newTask();
        } else {
            return super.newTaskFor(callable);
        }
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

}
