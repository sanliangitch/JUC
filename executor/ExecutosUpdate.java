package executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 对通过标准工程方法创建的 Executor 进行修改
 *
 * @author wulang
 * @create 2020/1/14/17:21
 */
public class ExecutosUpdate {

    public ExecutosUpdate(){
        ExecutorService exec = Executors.newCachedThreadPool();
        if (exec instanceof ThreadPoolExecutor){
            ((ThreadPoolExecutor)exec).setCorePoolSize(10);
        }else {
            throw new AssertionError("Oops, bad assumption");
        }
    }
}
