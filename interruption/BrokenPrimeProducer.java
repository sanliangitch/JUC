package interruption;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 不可靠的取消操作将把生产者至于阻塞操作中（不要这么做）
 * <p>
 * 生产者线程生成素数，并将它们放入一个阻塞队列。
 * 如果生产者的速度超过了消费者的处理速度，队列将被填满，put方法也会阻塞。
 * 当生产者在put方法中阻塞时，如果消费者希望取消生产者任务，那么将发生什么情况?
 * 它可以调用cancel方法来设置cancelled标志，但此时生产者却永远不能检查这个标志，
 * 因为它无法从阻塞的put方法中恢复过来(因为消费者此时已经停止从队列中取出素数，所以put方法将-直保持阻塞状态)。
 *
 * @author wulang
 * @create 2020/1/13/15:15
 */
public class BrokenPrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;
    private volatile boolean cancelled = false;

    BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!cancelled) {
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException consumed) {
            // TODO
        }
    }

    public void cancel() {
        cancelled = true;
    }

    /**
     * 每个线程都有一个boolean类型的中断状态。当中断线程时，这个线程的中断状态将被设置为true。
     * 在 Thread 中包含了中断线程以及查询线程中断状态的方法。
     * interrupt方法能中断目标线程，而isInterrupted方法能返回目标线程的中断状态。
     * 静态的interrupted方法将清除当前线程的中断状态，并返回它之前的值，这也是清除中断状态的唯一方法。
     *
     * @throws InterruptedException
     */
    void consumePrimes() throws InterruptedException {
        BlockingQueue<BigInteger> primes = new BlockingQueue<BigInteger>() {
            @Override
            public boolean add(BigInteger bigInteger) {
                return false;
            }

            @Override
            public boolean offer(BigInteger bigInteger) {
                return false;
            }

            @Override
            public void put(BigInteger bigInteger) throws InterruptedException {

            }

            @Override
            public boolean offer(BigInteger bigInteger, long timeout, TimeUnit unit) throws InterruptedException {
                return false;
            }

            @Override
            public BigInteger take() throws InterruptedException {
                return null;
            }

            @Override
            public BigInteger poll(long timeout, TimeUnit unit) throws InterruptedException {
                return null;
            }

            @Override
            public int remainingCapacity() {
                return 0;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public int drainTo(Collection<? super BigInteger> c) {
                return 0;
            }

            @Override
            public int drainTo(Collection<? super BigInteger> c, int maxElements) {
                return 0;
            }

            @Override
            public BigInteger remove() {
                return null;
            }

            @Override
            public BigInteger poll() {
                return null;
            }

            @Override
            public BigInteger element() {
                return null;
            }

            @Override
            public BigInteger peek() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public Iterator<BigInteger> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends BigInteger> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
        boolean needMorePrimes = false;
        BrokenPrimeProducer producer = new BrokenPrimeProducer(primes);
        producer.start();
        try {
            while (needMorePrimes) {
                consume(primes.take());
            }
        } finally {
            producer.cancel();
        }
    }

    private void consume(BigInteger take) {

    }
}
