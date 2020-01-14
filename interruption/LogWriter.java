package interruption;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 停止基于线程的服务
 * <p>
 * 日志服务
 * <p>
 * 不支持关闭的生产者——消费者日志服务
 * <p>
 * 在程序清单的LogWriter中给出了一个简单的日志服务示例，其中日志操作在单独的日志线程中执行。
 * 产生日志消息的线程并不会将消息直接写入输出流，而是由LogWriter通过BlockingQueue将消息提交给日志线程，
 * 并由日志线程写入。
 * 这是一种多生产者单消费者(Multiple-Producer,Single-Consumer) 的设计方式:
 * 每个调用log的操作都相当于一个生产者，而后台的日志线程则相当于消费者。
 * 如果消费者的处理速度低于生产者的生成速度，那么.BlockingQueue将阻塞生产者，
 * 直到日志线程有能力处理新的日志消息。
 *
 * @author wulang
 * @create 2020/1/13/22:22
 */
public class LogWriter {
    // 拥塞队列作为缓存区
    private final BlockingQueue<String> queue;
    // 日志线程
    private final LoggerThread logger;
    // 队列大小
    private static final int CAPACITY = 1000;

    volatile boolean shutdownRequested = false;

    public LogWriter(Writer writer) {
        this.queue = new LinkedBlockingDeque<String>(CAPACITY);
        this.logger = new LoggerThread(writer);
    }

    public void start() {
        logger.start();
    }

    public void log(String msg) throws InterruptedException {
        queue.put(msg);
    }

    /**
     * 通过一种不可靠的方式为日志服务i增加关闭支持
     * <p>
     * 另一种关闭LogWriter的方法是:设置某个“已请求关闭”标志，以避免进一步提交日志消息，
     * 如 log1 所示。在收到关闭请求后，消费者会把队列中的所有消息写入日志，
     * 并解除所有在调用log时阻塞的生产者。
     * 然而，在这个方法中存在着竟态条件问题，使得该方法并不可靠。
     * log 的实现是一种“先判断再运行”的代码序列:生产者发现该服务还没有关闭，
     * 因此在关闭服务后仍然会将日志消息放入队列，这同样会使得生产者可能在调用log时阻塞并且无法解除阻塞状态。
     * 可以通过一些技巧来降低这种情况的发生概率( 例如，在宣布队列被清空之前，让消费者等待数秒钟)，但这些都没有解决问题的本质，
     * 即使很小的概率也可能导致程序发生故障。
     *
     * @param msg
     * @throws InterruptedException
     */
    public void log1(String msg) throws InterruptedException {
        if (!shutdownRequested) {
            queue.put(msg);
        } else {
            throw new IllegalStateException("logger is shut down");
        }
    }

    private class LoggerThread extends Thread {
        private final PrintWriter writer;

        public LoggerThread(Writer writer) {
            this.writer = (PrintWriter) writer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    writer.println(queue.take());
                }
            } catch (InterruptedException ignored) {

            } finally {
                writer.close();
            }
        }
    }
}
