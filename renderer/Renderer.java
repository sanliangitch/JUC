package renderer;

import com.sun.scenario.effect.ImageData;

import java.util.List;
import java.util.concurrent.*;

/**
 * 使用ExecutorCompletionService，是页面元素在下载完成后立刻显示出来
 *
 * 为每一幅图像的下载都创建一个独立任务，并在线程池中执行它们，从而将串行的下载过程转换为并行的过程:这将减少下载所有图像的总时间。
 * 此外，通过从CompletionService中获取结果以及使每张图片在下载完成后立刻显示出来，能使用户获得一个更加动态和更高响应性的用户界面。
 * @author wulang
 * @create 2020/1/10/14:54
 */
public class Renderer {
    /**
     * 指定时间完成任务参数
     */
    private static final long TIME_BUDGET = 10000L;
    /**
     * 默认的广告
     */
    private static final Ad DEFULT_AD = null;
    private final ExecutorService executor;
    Renderer(ExecutorService executor){
        this.executor = executor;
    }
    void renderPage(CharSequence source){
        List<ImageInfo> info = scanForImageInfo(source);
        ExecutorCompletionService<ImageData> completionService =
                new ExecutorCompletionService<>(executor);
        for (final ImageInfo imageInfo : info){
            completionService.submit(imageInfo::downloadImage);
        }
        renderText(source);
        try {
        for (int t = 0,n = info.size();t < n; t++){
            Future<ImageData> f = completionService.take();
            ImageData imageData = f.get();
            renderImage(imageData);
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e){
            throw launderThrowable(e.getCause());
        }
    }

    /**
     * 在指定时间内获取广告信息
     *
     * Future.get的一种典型应用。在它生成的页面中包括响应用户请求的内容以及从广告服务器上获得的广告。它将获取广告的任务提交给一个Executor,
     * 然后计算剩余的文本页面内容，最后等待广告信息，直到超出指定的时间。如果get超时，那么将取消广告获取任务，并转而使用默认的广告信息。
     */
    Page renderPageWithAd() throws InterruptedException{
        long endNanos = System.nanoTime() + TIME_BUDGET;
        Future<Ad> f = (Future<Ad>) executor.submit(new FetchAdTask());
        //在等待广告的同时显示页面
        Page page = renderPageBody();
        Ad ad;
        try {
            long timeLeft = endNanos - System.nanoTime();
            ad = f.get(timeLeft, TimeUnit.NANOSECONDS);
        } catch (ExecutionException e) {
            ad = DEFULT_AD;
        } catch (TimeoutException e) {
            ad = DEFULT_AD;
            f.cancel(true);
        }
        page.setAd(ad);
        return page;
    }

    private Page renderPageBody() {
        return null;
    }

    private void renderImage(ImageData imageData) {

    }

    /**
     *  如果是 Throwable 是 Error，那么抛出它；
     *  如果是 RuntimeException ，那么返回它，否则抛出 IllegalStateException
     *
     * @param t
     * @return
     */
    public static RuntimeException launderThrowable(Throwable t){
        if (t instanceof RuntimeException){
            return (RuntimeException)t;
        }else if (t instanceof Error){
            throw (Error)t;
        }else {
            throw new IllegalStateException("Not unchecked",t);
        }
    }

    private void renderText(CharSequence source) {

    }

    private List<ImageInfo> scanForImageInfo(CharSequence source) {
        return null;
    }
}

class Page{

    public void setAd(Ad ad) {

    }
}

class FetchAdTask implements Runnable{

    @Override
    public void run() {

    }
}

class Ad{

}