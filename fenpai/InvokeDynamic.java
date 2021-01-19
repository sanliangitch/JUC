package fenpai;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * 方法分派规则
 *
 * @author wulang
 * @create 2021/1/3/17:03
 */
public class InvokeDynamic {
    class GranFather {
        void thinking() throws Throwable {
            System.out.println("i am grandfather");
        }
    }

    class Father extends GranFather {
        @Override
        void thinking() throws Throwable {
            System.out.println("i am father");
        }
    }

    class Son extends Father {
        @Override
        void thinking() {
            // 实现调用祖父类的thinking()方法,打印 i am grandfather
            try {
                MethodType mt = MethodType.methodType(void.class);
                Field lookupImpl = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                lookupImpl.setAccessible(true);
                MethodHandles.Lookup lookup = (MethodHandles.Lookup) lookupImpl.get(null);
                MethodHandle mh = lookup.findSpecial(GranFather.class, "thinking", mt, GranFather.class);
                mh.invoke(this);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        (new InvokeDynamic().new Son()).thinking();
    }
}
