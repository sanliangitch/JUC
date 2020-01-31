package gc;

/**
 * -XX:NewSize=5242880 -XX:MaxNewSize=5242880 -XX:InitialHeapSize=10485760 -XX:MaxHeapSize=10485760 -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=10485760 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log
 * <p>
 * “-XX:InitialHeapSize"和“- XX:MaxHeapSize"就是初始堆大小和最大堆大小
 * “-XX:NewSize"和“-XX:MaxNewSize"是初始新生代大小和最大新生代大小，
 * “-XX:PretenureSizeThreshold=10485760"指定了大对象阈值是10MB。
 * 相当于给堆内存分配10MB内存空间，其中新生代是5MB内存空间，其中Eden区占4MB，每个Survivor区占0.5MB，大
 * 对象必须超过10MB才会直接进入老年代，年轻代使用ParNew垃圾回收器，老年代使用CMS垃圾回收器。
 * <p>
 * - XX:+PrintGCDetils:打印详细的gc日志
 * -XX:+PrintGCTimeStamps:这个参数可以打印出来每次GC发生的时间
 * -Xloggc:gc.log:这个参数可以设置将gc日志写入-个磁盘文件
 *
 * @author wulang
 * @create 2020/1/31/20:07
 */
public class YoungGC {
    public static void main(String[] args) {
        byte[] array1 = new byte[1024 * 1024];
        array1 = new byte[1024 * 1024];
        array1 = new byte[1024 * 1024];
        array1 = null;

        byte[] array2 = new byte[1024 * 1024 * 2];
    }
}
