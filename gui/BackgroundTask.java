package gui;

import java.util.concurrent.*;

/**
 * 支持取消，完成通知以及进度通知的后台任务类
 * <p>
 * 通过Future来表示一个长时间的任务，可以极大地简化取消操作的实现。
 * 在FutureTask中也有一个done方法同样有助于实现完成通知。当后台的Callable完成后，将调用done。
 * 通过done方法在事件线程中触发一个完成任务，我们能够构造一个 BackgroundTask 类，
 * 这个类将提供一个在事件线程中调用的onCompletion方法。
 *
 * @author wulang
 * @create 2020/1/15/11:46
 */
abstract class BackgroundTask<V> implements Runnable, Future<V> {
    /**
     * BackgroundTask还支持进度标识。compute 方法可以调用setProgress方法以数字形式来指示进度。
     * 因而在事件线程中调用onProgress,从而更新用户界面以显示可视化的进度信息。
     * 要想实现BackgroundTask,你只需要实现compute,该方法将在后台线程中调用。
     * 也可以改写onCompletion和onProgress,这两个方法也会在事件线程中调用。
     */
    private final FutureTask<V> computation = new Computation();

    private class Computation extends FutureTask<V> {
        public Computation() {
            super(() -> BackgroundTask.this.compute());
        }

        @Override
        protected final void done() {
            GuiExecutor.instance().execute(() -> {
                V value = null;
                Throwable thrown = null;
                boolean cancelled = false;
                try {
                    value = get();
                } catch (ExecutionException e) {
                    thrown = e.getCause();
                } catch (CancellationException e) {
                    cancelled = true;
                } catch (InterruptedException consumed) {

                } finally {
                    onCompletion(value, thrown, cancelled);
                }
            });
        }
    }

    protected void setProgress(final int current, final int max) {
        GuiExecutor.instance().execute(() -> onProgress(current, max));
    }

    /**
     * 在后台线程中背取消
     *
     * @return
     * @throws Exception
     */
    protected abstract V compute() throws Exception;

    /**
     * 在事件线程中被取消
     *
     * @param result
     * @param exception
     * @param cancelled
     */
    protected void onCompletion(V result, Throwable exception, boolean cancelled) {
    }

    protected void onProgress(int current, int max) {
    }
    //Future 的其他方法
}
