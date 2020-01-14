package interruption;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * @author wulang
 * @create 2020/1/13/23:16
 */
public class CrawlerThread extends Thread {
    private static final File POISON = new File("毒丸对象");
    private final BlockingQueue<File> queue;
    private final FileFilter fileFilter;
    private final File root;
    public CrawlerThread(BlockingQueue<File> queue,FileFilter fileFilter,File root){
        this.queue = queue;
        this.fileFilter = fileFilter;
        this.root = root;
    }
    @Override
    public void run() {
        try {
            crawl(root);
        }catch (InterruptedException e){
            // 发生异常
        }finally {
            while (true){
                try {
                    queue.put(POISON);
                    break;
                }catch (InterruptedException e1){
                    //重新尝试
                }
            }
        }
    }
    private void crawl(File root)throws InterruptedException{

    }
}
