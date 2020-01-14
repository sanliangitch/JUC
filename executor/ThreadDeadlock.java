package executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *  在单线程 Executors 中任务发生死锁（不要这么做）
 *
 * @author wulang
 * @create 2020/1/14/16:39
 */
public class ThreadDeadlock {
    ExecutorService exec = Executors.newSingleThreadExecutor();
    public class renderPageTask implements Callable<String>{
        @Override
        public String call() throws Exception {
            Future<String> header,footer;
            header = exec.submit(new LoadFileTask("header.html"));
            footer = exec.submit(new LoadFileTask("footer.html"));
            String page = renderBody();
            //将发生死锁 —— 由于任务在等待子任务的结果
            return header.get() + page + footer.get();
        }
    }

    public String renderBody(){
        return null;
    }
}
class LoadFileTask implements Callable<String>{
    String name;
    public LoadFileTask(String name){
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        return null;
    }
}