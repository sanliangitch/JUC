package interruption;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 使用 ExecutorService 的日志服务
 * <p>
 * 简单的程序可以直接在main函数中启动和关闭全局的ExecutorService.而在复杂程序中，
 * 通常会将ExecutorService封装在某个更高级别的服务中，并且该服务能提供其自己的生命周期方法，
 * 例如程序中LogService的一种变化形式，它将管理线程的工作委托给一个ExecutorService，而不是由其自行管理。
 * 通过封装ExecutorService，可以将所有权链(Ownership Chain)从应用程序扩展到服务以及线程，
 * 所有权链上的各个成员都将管理它所拥有的服务或线程的生命周期。
 *
 * @author wulang
 * @create 2020/1/13/23:03
 */
public class LogService2 {
    private static final TimeUnit INIT = null;
    private long TIMEOUT;
    EventExecutorGroup parent;
    ThreadFactory threadFactory;
    boolean addTaskWakesUp;
    Writer writer;
    private final ExecutorService exec = new SingleThreadEventExecutor(parent, threadFactory, addTaskWakesUp) {
        @Override
        protected void run() {

        }
    };

    public void stare() {
    }

    public void stop() throws InterruptedException {
        try {
            exec.shutdown();
            exec.awaitTermination(TIMEOUT, INIT);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void log(String msg) {
        try {
            exec.execute(new WriterTask(msg));
        } catch (RejectedExecutionException ignored) {

        }
    }
}

class WriterTask extends Thread {
    public WriterTask(String msg) {

    }
}