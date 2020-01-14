package interruption;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * @author wulang
 * @create 2020/1/13/23:17
 */
public class IndexingThread extends Thread{
    private static final File POISON = new File("毒丸对象");
    private final BlockingQueue<File> queue;
    private final FileFilter fileFilter;
    private final File root;
    public IndexingThread(BlockingQueue<File> queue,FileFilter fileFilter,File root){
        this.queue = queue;
        this.fileFilter = fileFilter;
        this.root = root;
    }
    @Override
    public void run() {
        try {
            while (true){
                File file = queue.take();
                if (file == POISON){
                    break;
                }else {
                    indexFile(file);
                }
            }
        }catch (InterruptedException consumed){

        }
    }

    private void indexFile(File file) {

    }
}
