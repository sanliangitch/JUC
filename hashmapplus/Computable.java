package hashmapplus;

/**
 * 开发一个高效且可伸缩的结果缓存
 *
 * @author wulang
 * @create 2020/1/9/15:30
 */
public interface Computable<A,V> {
    V comput(A arg) throws InterruptedException;
}
