package hashmapplus;

import java.util.HashMap;
import java.util.Map;

/**
 * 先从HashMap开始，然后分析并发性缺陷，并讨论如何修复他们
 *
 * @create 2020/1/9/15:34
 */
public class Memoizerl<A,V> implements Computable<A,V>{
//    @GuardedBy("this")
    private final Map<A,V> cache = new HashMap<A,V>();
    private final Computable<A,V> c;

    public Memoizerl(Computable<A,V> c){
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
