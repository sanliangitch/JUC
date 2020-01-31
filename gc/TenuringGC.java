package gc;

/**
 * 15次GC后进入老年代
 * <p>
 * -XX:NewSize=5242880 -XX:MaxNewSize=5242880 -XX:InitialHeapSize=10485760 -XX:MaxHeapSize=10485760 -XX:SurvivorRatio=4 -XX:PretenureSizeThreshold=10485760 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:TenuringGC.log
 *
 * @author wulang
 * @create 2020/1/31/21:44
 */
public class TenuringGC {
    public static void main(String[] args) {
        int age = 16;
        byte[] array = new byte[128 * 1024];

        while (age >= 0) {
            byte[] array1 = new byte[2 * 1024 * 1024];
            array1 = new byte[2 * 1024 * 1024];
            array1 = new byte[2 * 1024 * 1024];
            array1 = null;
            byte[] array3 = new byte[2 * 1024 * 1024];
            age--;
        }
    }
}
