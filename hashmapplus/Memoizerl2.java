package hashmapplus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 第一次改进：用 ConcurrentHashMap<A,V> 代替 HashMap<A,V> 来改进前面的并发行为
 *
 *  存在的问题：因为缓存的作用是避免相同的数据被计算多次。
 *             但当两个线程同时调用 comput 时存在漏洞，可能会导致计算得到相同的值
 * @author wulang
 * @create 2020/1/9/15:47
 */
public class Memoizerl2<A,V> implements Computable<A,V> {
    private final Map<A,V> cache = new ConcurrentHashMap<A,V>();
    private final Computable<A,V> c;

    public Memoizerl2(Computable<A,V> c){
        this.c = c;
    }
    @Override
    public V comput(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null){
            result= c.comput(arg);
            cache.put(arg,result);
        }
        return result;
    }
}
