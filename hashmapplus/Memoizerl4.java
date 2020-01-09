package hashmapplus;

import java.util.Map;
import java.util.concurrent.*;

/**
 *  第三次改进：使用 ConcurrentHashMap 中的原子方法 putIfAbsent ，避免 Memoizerl3 的漏洞
 *
 * @author wulang
 * @create 2020/1/9/16:16
 */
public class Memoizerl4<A, V> implements Computable<A,V>{
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
    private final Computable<A,V> c;

    public Memoizerl4(Computable<A,V> c){
        this.c = c;
    }
    @Override
    public V comput(A arg) throws InterruptedException {
        while (true){
            Future<V> f = cache.get(arg);
            Callable<V> eval = new Callable<V>() {
                @Override
                public V call() throws InterruptedException {
                    return c.comput(arg);
                }
            };
            FutureTask<V> ft = new FutureTask<>(eval);
            f = cache.putIfAbsent(arg,ft);
            if (f == null){
                f = ft;
                ft.run();
            }
            try {
                return f.get();
            }catch (CancellationException e){
                /**
                 * 当缓存的是 Future 而不是值时，将导致缓存污染问题：
                 * 如果某个计算取消或失败，那么计算这个结果是将指明计算过程被取消或者失败。
                 */
                cache.remove(arg,f);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
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
