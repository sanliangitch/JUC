package gc;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Metaspace内存溢出
 * -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m
 * <p>
 * -XX:+UseParNewGC
 * -XX:+UseConcMarkSweepGC
 * -XX:MetaspaceSize=10M
 * -XX:MaxMetaspaceSize=10M
 * -XX:+PrintGCDetails
 * -Xloggc:gc.log
 * -XX:+HeapDumpOnOutOfMemoryError
 * -XX:HeapDumpPath=./
 *
 * @author wulang
 * @create 2020/2/5/18:04
 */
public class MetaspaceGC {
    public static void main(String[] args) {
        long counter = 0;
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(Car.class);
            enhancer.setUseCache(false);
            enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
                if (method.getName().equals("run")) {
                    System.out.println("启动汽车之前，先进行自动安全检查");
                    return methodProxy.invokeSuper(o, objects);
                } else {
                    return methodProxy.invokeSuper(o, objects);
                }
            });
            Car car = (Car) enhancer.create();
            car.run();
            System.out.println("目前创建了" + (++counter) + "个Car类的子类了");
        }
    }

    static class Car {
        public void run() {
            System.out.println("汽车启动，开始行驶......");
        }
    }
}
