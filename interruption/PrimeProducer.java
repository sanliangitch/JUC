package interruption;

import javafx.concurrent.Task;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * 通过中断来取消
 * <p>
 * BrokenPrimeProducer中的问题很容易解决(和简化) :使用中断而不是boolean标志来请求取消。
 * 在每次迭代循环中，有两个位置可以检测出中断:
 * 在阻塞的put方法调用中，以及在循环开始处查询中断状态时。
 * 由于调用了阻塞的put方法，因此这里并不一定需要进行显式的检测，
 * 但执行检测却会使PrimeProducer对中断具有更高的响应性，
 * 因为它是在启动寻找素数任务之前检查中断的，而不是在任务完成之后。
 * 如果可中断的阻塞方法的调用频率并不高，不足以获得足够的响应性，那么显式地检测中断状态能起到一定的帮助作用。
 *
 * @author wulang
 * @create 2020/1/13/15:34
 */
public class PrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;

    PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted()) {
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException consumed) {
            //允许线程退出
        }
    }

    public void cancel() {
        interrupt();
    }

    /**
     * 不可取消的任务在退出前恢复中断
     *
     * @param queue
     * @return
     */
    public Task getNextTask(BlockingQueue<Task> queue) {
        boolean interrupted = false;
        try {
            while (true){
                try {
                    return queue.take();
                }catch (InterruptedException e){
                    interrupted = true;
                    //重新尝试
                }
            }
        }finally {
            if (interrupted){
                Thread.currentThread().interrupt();
            }
        }
    }
}
