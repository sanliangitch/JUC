package interruption;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * 另一种关闭生产者-消费者服务的方式就是使用“毒丸(Poison Pill)” 对象:“毒丸”是指一个放在队列上的对象，
 * 其含义是:“当得到这个对象时，立即停止。”
 * 在FIFO (先进先出)队列中，“毒丸”对象将确保消费者在关闭之前首先完成队列中的所有工作，在提交“毒丸”对象之前提交的所有工作都会被处理，
 * 而生产者在提交了“毒丸”对象后，将不会再提交任何工作。
 * <p>
 * 只有在生产者和消费者的数量都已知的情况下，才可以使用“毒丸”对象。
 * 在Indexing-Service中采用的解决方案可以扩展到多个生产者:只需每个生产者都向队列中放入一个“毒丸”对象，
 * 并且消费者仅当在接收到N个“毒丸”对象时才停止。这种方法也可以扩展到多个消费者的情况，只需生产者将N个“毒丸”对象放入队列。
 * 然而，当生产者和消费者的数量较大时，这种方法将变得难以使用。只有在无界队列中，“毒丸”对象才能可靠地工作。
 *
 * @author wulang
 * @create 2020/1/14/11:01
 */
public class IndexingService {
    private static final File POISON = new File("毒丸对象");
    BlockingQueue<File> queue;
    FileFilter fileFilter;
    File root;
    private final IndexingThread consumer = new IndexingThread(queue, fileFilter, root);
    private final CrawlerThread produces = new CrawlerThread(queue, fileFilter, root);

    public void start() {
        produces.start();
        consumer.start();
    }

    public void stop() {
        produces.interrupt();
    }

    public void awaitTermination() throws InterruptedException {
        consumer.join();
    }
}
