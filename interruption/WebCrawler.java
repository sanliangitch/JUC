package interruption;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * WebCrawler中给出了TrackingExecutor的用法。
 * 网页爬虫程序的工作.通常是无穷尽的，因此当爬虫程序必须关闭时，我们通常希望保存它的状态，以便稍后重新启动。
 * CrawITask 提供了一个getPage方法，该方法能找出正在处理的页面。
 * 当爬虫程序关闭时,无论是还没有开始的任务，还是那些被取消的任务，都将记录它们的URL，因此当爬虫程序重新启动时，
 * 就可以将这些URL的页面抓取任务加入到任务队列中。
 *
 * @author wulang
 * @create 2020/1/14/13:34
 */
public abstract class WebCrawler {
    long timeout;
    TimeUnit unit;
    private volatile TrackingExecutor exec;
    private final Set<URL> urlsToCrawl = new HashSet<URL>();

    /**
     * 使用TrackingExecutorService来保存未完成的任务以备后续执行
     */
    public synchronized void start() {
        exec = new TrackingExecutor(Executors.newCachedThreadPool());
        for (URL url : urlsToCrawl) {
            //提交抓取任务
            submitCrawlTask(url);
        }
        urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            //保存未抓取
            saveUncrawled(exec.shutdownNow());
            if (exec.awaitTermination(timeout, unit)) {
                saveUncrawled(exec.getCancelledTasks());
            }
        } finally {
            exec = null;
        }
    }

    protected abstract List<URL> processPage(URL url);

    protected void saveUncrawled(List<Runnable> uncrawled) {
        for (Runnable task : uncrawled) {
            urlsToCrawl.add(((CrawTask) task).getPage());
        }
    }

    ;

    protected void submitCrawlTask(URL u) {
        exec.execute(new CrawTask(u));
    }

    ;

    private class CrawTask implements Runnable {
        private final URL url;

        public CrawTask(URL url) {
            this.url = url;
        }

        @Override
        public void run() {
            for (URL link : processPage(url)) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                submitCrawlTask(link);
            }
        }

        public URL getPage() {
            return url;
        }
    }
}
