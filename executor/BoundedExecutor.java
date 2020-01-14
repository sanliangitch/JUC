package executor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * 使用 Semaphore(信号量) 来控制任务的提交速率
 * <p>
 * 该方法使用了一个无界队列(因为不能限制队列的大小和任务的到达率),并设置信号量的上界设置为线程池的大小加
 * 上可排队任务的数量，这是因为信号量需要控制正在执行的和等待执行的任务数量。
 *
 * @author wulang
 * @create 2020/1/14/16:48
 */
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor exec, Semaphore semaphore) {
        this.exec = exec;
        this.semaphore = semaphore;
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            exec.execute(() -> {
                try {
                    command.run();
                } finally {
                    semaphore.release();
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
        }
    }
}
