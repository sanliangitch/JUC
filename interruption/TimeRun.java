package interruption;

import java.util.concurrent.*;

/**
 * 计时运行
 * <p>
 * 许多问题永远也无法解决(例如，枚举所有的素数)，而某些问题，能很快得到答案，也可能永远得不到答案。
 * 在这些情况下，如果能够指定“最多花10分钟搜索答案"或者“枚举出在10分钟内能找到的答案”，那么将是非常有用的。
 *
 * @author wulang
 * @create 2020/1/13/15:48
 */
public class TimeRun {
    private static final ScheduledExecutorService cancelExec = new ScheduledThreadPoolExecutor(10);

    /**
     * 在外部线程中安排中断（不要这么做）
     * <p>
     * 这是一种非常简单的方法，但却破坏了以下规则:在中断线程之前，应该了解它的中断策略。
     * 由于timedRun可以从任意一个线程中调用，因此它无法知道这个调用线程的中断策略。
     * 如果任务在超时之前完成，那么中断timedRun所在线程的取消任务将在timedRun返回到调用者之后启动。
     * 我们不知道在这种情况下将运行什么代码，但结果一定是不好的。
     * (可以使用schedule返回的ScheduledFuture来取消这个取消任务以避免这种风险，这种做法虽然可行，但却非常复杂。)
     *
     * @param r
     * @param timeout
     * @param unit
     * @throws InterruptedException
     */
    public static void timedRun(final Runnable r,
                                long timeout, TimeUnit unit)
            throws InterruptedException {
        final Thread taskThread = Thread.currentThread();
        cancelExec.schedule(taskThread::interrupt, timeout, unit);
        r.run();
    }

    /**
     * 在专门的线程中中断任务
     * <p>
     * 下面代码中解决了aSecondOfPrimes的异常处理问题以及之前解决方案中的问题。
     * 执行任务的线程拥有自己的执行策略，即使任务不响应中断，限时运行的方法仍能返回到它的调用者。
     * 在启动任务线程之后，timedRun 将执行一个限时的join方法。
     * 在join返回后，它将检查任务中是否有异常抛出，如果有的话，则会在调用timedRun的线程中再次抛出该异常。
     * 由于Throwable将在两个线程之间共享，因此该变量被声明为volatile类型，从而确保安全地将其从任务线程发布到timedRun线程。
     *
     * @param r
     * @param timeout
     * @param unit
     * @throws InterruptedException
     */
    public static void timeRunThread(final Runnable r,
                                     long timeout, TimeUnit unit)
            throws InterruptedException {
        class RethrowableTask implements Runnable {
            private volatile Throwable t;

            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (t != null) {
                    throw launderThrowable(t);
                }
            }
        }
        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancelExec.schedule(taskThread::interrupt, timeout, unit);
        taskThread.join(unit.toMillis(timeout));
        task.rethrow();
    }

    /**
     * 通过 Future 来取消任务
     * <p>
     * 将任务提交给一个ExecutorService, 并通过一个定时的Future.get来获得结果。
     * 如果get在返回时抛出了一个TimeoutException，那么任务将通过它的Future来取消。
     * (为了简化代码，这个版本的timedRun在finally块中将直接调用Future.cancel,因为取消一个已完成的任务不会带来任何影响。)
     * 如果任务在被取消前就抛出一个异常，那么该异常将被重新抛出以便由调用者来处理异常。
     * 在程序清单中还给出了另一种良好的编程习惯:取消那些不再需要结果的任务。
     *
     * @param r
     * @param timeout
     * @param unit
     * @throws InterruptedException
     */
    public static void timedRunFuture(final Runnable r,
                                      long timeout, TimeUnit unit)
            throws InterruptedException {
        Future<?> task = cancelExec.submit(r);
        try {
            task.get(timeout, unit);
        } catch (TimeoutException e) {
            //接下来的任务将被取消
        } catch (ExecutionException e) {
            //如果在任务中抛出异常，那么重新抛出该异常
            throw launderThrowable(e.getCause());
        } finally {
            //如果任务已经结束，那么执行取消操作也不回带来任何影响
            task.cancel(true);//如果任务正在运行，那么将被中断
        }
    }

    /**
     * 如果是 Throwable 是 Error，那么抛出它；
     * 如果是 RuntimeException ，那么返回它，否则抛出 IllegalStateException
     *
     * @param t
     * @return
     */
    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalStateException("Not unchecked", t);
        }
    }
}
