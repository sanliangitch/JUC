package concurrency.example.singleton;

/**
 * 枚举模式：最安全
 * <p>
 * 相对于饿汉模式，它是调用的时候才创建
 * 相对于懒汉模式，它是线程安全的
 *
 * @author wulang
 * @create 2020/1/20/13:31
 */
public class SingletonExample {

    // 私有构造函数
    private SingletonExample() {

    }

    public static SingletonExample getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        private SingletonExample singleton;

        // JVM保证这个方法绝对只调用一次
        Singleton() {
            singleton = new SingletonExample();
        }

        public SingletonExample getInstance() {
            return singleton;
        }
    }
}
