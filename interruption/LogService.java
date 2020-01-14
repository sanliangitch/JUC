package interruption;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 * 向 LogWriter 添加可靠的取消操作
 * <p>
 * 为LogWriter提供可靠关闭操作的方法是解决竞态条件问题，因而要使日志消息的提交操作成为原子操作。
 * 然而，我们不希望在消息加入队列时去持有一个锁，因为put方法本身就可以阻塞。
 * 我们采用的方法是:通过原子方式来检查关闭请求，并且有条件地递增-一个计数器来“保持”提交消息的权利，如程序所示。
 *
 * @author wulang
 * @create 2020/1/13/22:50
 */
public class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    private boolean isShutdown;
    private int reservations;

    public LogService(BlockingQueue<String> queue, LoggerThread loggerThread, PrintWriter writer) {
        this.queue = queue;
        this.loggerThread = loggerThread;
        this.writer = writer;
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
            loggerThread.interrupt();
        }
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown) {
                throw new IllegalStateException("");
            }
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (LogService.this) {
                        if (isShutdown && reservations == 0) {
                            break;
                        }
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            --reservations;
                        }
                        writer.print(msg);
                    }
                } catch (InterruptedException e) {
                    // retry
                } finally {
                    writer.close();
                }
            }
        }
    }
}

