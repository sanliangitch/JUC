package interruption;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * SocketUsingTask实现了CancellableTask, 并定义了Future.cancel 来关闭套接字和调用super.cancel。
 * 如果SocketUsingTask通过其自己的Future来取消，那么底层的套接字将被关闭并且线程将被中断。
 * 因此它提高了任务对取消操作的晌应性:不仅能够在调用可中断方法的同时确保响应取消操作，
 * 而且还能调用可阻调的套接字I/O方法。
 *
 * @author wulang
 * @create 2020/1/13/22:06
 */
public abstract class SocketUsingTask<T> implements CancellableTask<T> {
    private Socket socket;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    @Override
    public synchronized void cancel() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {

        }
    }

    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}
