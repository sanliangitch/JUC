package hashmapplus;

import java.math.BigInteger;

/**
 * 创建一个 ExpensiveFunction 包装器，帮助记住之前的运算结果，并将缓存结果封装起来
 *
 * @author wulang
 * @create 2020/1/9/15:32
 */
public class ExpensiveFunction implements Computable<String, BigInteger>{
    @Override
    public BigInteger comput(String arg) throws InterruptedException {
        //此处模拟经过长时间的运算
        return new BigInteger(arg);
    }
}
