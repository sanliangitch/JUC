package gc;

import java.util.ArrayList;
import java.util.List;

/**
 * -Xms10M -Xmx10m -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -Xloggc:gc1.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./
 *
 * @author wulang
 * @create 2020/2/5/21:16
 */
public class DumpGC {
    public static void main(String[] args) {
        long counter = 0;
        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new Object());
            System.out.println("当前创建了" + (++counter) + "个对象");
        }
    }
}
