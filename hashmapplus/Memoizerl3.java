package hashmapplus;

import java.util.Map;
import java.util.concurrent.*;

/**
 *  第二次改进：用 ConcurrentHashMap<A, Future<V>>() 替换 ConcurrentHashMap<A,V>
 *
 *      首先检查某个相应的计算是否已经开始（Memoizerl2 与之相反，它首先判断某个计算是否已经完成）。
 *      如果还没有启动，那么就创建一个 FutureTask ，并注册到 Map 中，然后启动计算；
 *      如果已经启动，那么等待现有的计算结果。
 *      结果可能很快会得到，也可能还在计算过程中，但这对于 Futrue.get 的调用者来说是透明的。
 *
 *
 *  由于 comput 方法中的 if 代码仍是非原子的“先检查在执行”操作，因此两个线程仍有可能在同意时间调用 comput 来计算相同的值。
 * @author wulang
 * @create 2020/1/9/15:52
 */
public class Memoizerl3<A,V> implements Computable<A,V>  {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
    private final Computable<A,V> c;

    public Memoizerl3(Computable<A,V> c){
        this.c = c;
    }
    @Override
    public V comput(A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if (f == null){
            Callable<V> eval = new Callable<V>() {
                @Override
                public V call() throws InterruptedException {
                    return c.comput(arg);
                }
            };
            FutureTask<V> ft = new FutureTask<>(eval);
            f = ft;
            cache.put(arg,ft);
            ft.run(); //在这里将调用 c.comput
        }
        try {
            return f.get();
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

    /**
     *  如果是 Throwable 是 Error，那么抛出它；
     *  如果是 RuntimeException ，那么返回它，否则抛出 IllegalStateException
     *
     * @param t
     * @return
     */
    public static RuntimeException launderThrowable(Throwable t){
        if (t instanceof RuntimeException){
            return (RuntimeException)t;
        }else if (t instanceof Error){
            throw (Error)t;
        }else {
            throw new IllegalStateException("Not unchecked",t);
        }
    }
}
