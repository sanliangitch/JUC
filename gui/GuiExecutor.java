package gui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wulang
 * @create 2020/1/15/12:14
 */
public class GuiExecutor extends AbstractExecutorService {
    private static final GuiExecutor instance = new GuiExecutor();

    private GuiExecutor() {
    }

    public static GuiExecutor instance() {
        return instance;
    }

    @Override
    public void execute(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    //其他生命周期方法的实现

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }
}
