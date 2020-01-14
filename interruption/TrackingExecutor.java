package interruption;

import java.util.*;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * shutdownNow的局限性
 * <p>
 * TrackingExecutor中给出了如何在关闭过程中判断正在执行的任务。
 * 通过封装ExecutorService并使得execute ( 类似地还有submit,在这里没有给出)记录哪些任务是在关闭后取消的，
 * TrackingExecutor 可以找出哪些任务已经开始但还没有正常完成。
 * 在Executor结束后，getCancelledTasks 返回被取消的任务清单。
 * 要使这项技术能发挥作用，任务在返回时必须维持线程的中断状态，在所有设计良好的任务中都会实现这个功能。
 *
 * @author wulang
 * @create 2020/1/14/11:46
 */
public class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final Set<Runnable> tasksCancelledAtShutdown =
            Collections.synchronizedSet(new HashSet<Runnable>());

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }

    /**
     * 获取已取消的任务
     * <p>
     * 在 ExecutorService 中跟踪在关闭之后被取消的任务
     *
     * @return
     */
    public List<Runnable> getCancelledTasks() {
        if (!exec.isTerminated()) {
            throw new IllegalStateException();
        }
        return new ArrayList<Runnable>(tasksCancelledAtShutdown);
    }

    /**
     * 执行
     *
     * @param runnable
     */
    @Override
    public void execute(final Runnable runnable) {
        exec.execute(() -> {
            try {
                runnable.run();
            } finally {
                if (isShutdown() && Thread.currentThread().isInterrupted()) {
                    tasksCancelledAtShutdown.add(runnable);
                }
            }
        });
    }

    //将 ExecutorService 的其他方法委托给exec

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

}
