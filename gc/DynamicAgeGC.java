package gc;

/**
 * -XX:NewSize=10485760 -XX:MaxNewSize=10485760 -XX:InitialHeapSize=20971520 -XX:MaxHeapSize=20971520 -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=15 -XX:PretenureSizeThreshold=10485760 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:DynamicAgeGC.log
 *
 * @author wulang
 * @create 2020/1/31/20:57
 */
public class DynamicAgeGC {
    public static void main(String[] args) {
        byte[] array1 = new byte[2 * 1024 * 1024];
        array1 = new byte[2 * 1024 * 1024];
        array1 = new byte[2 * 1024 * 1024];
        array1 = null;

        byte[] array2 = new byte[1024 * 128];

        byte[] array3 = new byte[1024 * 1024 * 2];

        array3 = new byte[1024 * 1024 * 2];
        array3 = new byte[1024 * 1024 * 2];
        array3 = new byte[1024 * 128];
        array3 = null;

        byte[] array4 = new byte[2 * 1024 * 1024];
    }
}
