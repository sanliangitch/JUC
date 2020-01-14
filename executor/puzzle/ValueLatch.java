package executor.puzzle;

/**
 * 由 ConcurrentPuzzleSolver 使用携带结果的闭锁
 * <p>
 * 为了在找到某个解答后停止搜索，需要通过某种方式来检查是否有线程已经找到了一个解答。
 * 如果需要第一个找到的解答，那么还需要在其他任务都没有找到解答时更新解答。
 * 这些需求描述的是一种闭锁(Latch)机制，具体地说，是一种包含结果的闭锁。
 * 可以很容易地构造出一个阻塞的并且可携带结果的闭锁，但更简单且更不容易出错的方式是使用现有库中的类，
 * 而不是使用底层的语言机制。
 * 在程序ValueLatch中使用CountDownLatch来实现所需的闭锁行为，并且使用锁定机制来确保解答只会被设置一次。
 *
 * @author wulang
 * @create 2020/1/14/20:00
 */
public class ValueLatch<T> {
    /**
     * 流程说明：
     * ①：每个任务首先查询solution闭锁，找到一个解答就停止。
     * 而在此之前，主线程需要等待，ValueLatch中的getValue将一直阻塞，直到有线程设置了这个值。
     * ValueLatch 提供了一种方式来保存这个值，只有第一次调用才会设置它。
     * 调用者能够判断这个值是否已经被设置，以及阻塞并等候它被设置。在第一次调用setValue时，将更新解答方案，
     * 并且CountDownLatch会递减，从getValue中释放主线程。
     * ②：第一个找到解答的线程还会关闭Executor,从而阻止接受新的任务。要避免处理RejectedExecutionException,
     * 需要将拒绝执行处理器设置为“抛弃已提交的任务”。
     * 然后，所有未完成的任务最终将执行完成，并且在执行任何新任务时都会失败，从而使Executor结束。
     */
    private T value = null;
    private final java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(1);

    public boolean isSet() {
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this) {
            return value;
        }
    }
}
