package interruption;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 使用 volatile 类型的域来保存取消状态
 * <p>
 * 在Java中没有一种安全的抢占式方法来停止线程，因此也就没有安全的抢占式方法来停止任务。
 * 只有一些协作式的机制，使请求取消的任务和代码都遵循一种协商好的协议。
 *
 * @author wulang
 * @create 2020/1/13/14:59
 */
public class PrimeGenerator implements Runnable {

    private final List<BigInteger> primes = new ArrayList<BigInteger>();
    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<>(primes);
    }

    /**
     * 一个仅运行一秒钟的素数生成器
     *
     * @return
     * @throws InterruptedException
     */
    List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator).start();
        try {
            SECONDS.sleep(1);
        } finally {
            generator.cancel();
        }
        return generator.get();
    }
}
