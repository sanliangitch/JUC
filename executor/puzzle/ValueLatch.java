package executor.puzzle;

/**
 * 由 ConcurrentPuzzleSolver 使用携带结果的闭锁
 *
 * @author wulang
 * @create 2020/1/14/20:00
 */
public class ValueLatch<T> {
    private T value = null;
    private final java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(1);
    public boolean isSet() {
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()){
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException{
        done.await();
        synchronized (this){
            return  value;
        }
    }
}
