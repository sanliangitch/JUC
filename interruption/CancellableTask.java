package interruption;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * 采用 newTaskFor 来封装非标准的取消
 * <p>
 * CancellableTask中定义了一个CancellableTask接口，
 * 该接口扩展了Callable,并增加了一个cancel方法和一个newTask工厂方法来构造RunnableFuture。
 * CancellingExecutor扩展了ThreadPoolExecutor，并通过改写newTaskFor使得CancellableTask可以创建自己的Future.
 *
 * @author wulang
 * @create 2020/1/13/21:57
 */
public interface CancellableTask<T> extends Callable<T> {
    void cancel();

    RunnableFuture<T> newTask();
}
