package gc;

import java.util.concurrent.atomic.LongAdder;

/**
 * 栈溢出
 *
 * @author wulang
 * @create 2020/2/5/18:19
 */
public class StackGC {
    public static LongAdder longAdder = new LongAdder();
    public static void main(String[] args) {
        work();
    }
    public static void work(){
        longAdder.add(1);
        System.out.println("目前第" + longAdder +"次调用");
        work();
    }
}
