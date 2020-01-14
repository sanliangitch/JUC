package interruption;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 处理不可中断的阻塞
 * <p>
 * 在Java库中，许多可阻塞的方法都是通过提前返回或者抛出InterruptedException来响应中断请求的，
 * 从而使开发人员更容易构建出能响应取消请求的任务。然而，并非所有的可阻塞方法或者阻塞机制都能响应中断;
 * 如果-一个线程由于执行同步的Socket I/O或者等待获得内置锁而阻塞，那么中断请求只能设置线程的中断状态，
 * 除此之外没有其他任何作用。对于那些由于执行不可中断操作而被阻塞的线程，
 * 可以使用类似于中断的手段来停止这些线程，但这要求我们必须知道线程阻塞的原因。
 * <p>
 * ReaderThread给出了如何封装非标准的取消操作。
 * <p>
 * 通过改写 interrput 方法将非标准的取消操作封装在 Thread 中
 * <p>
 * ReaderThread管理了一个套接字连接，它采用同步方式从该套接字中读取数据，并将接收到的数据传递给processBuffer。
 * 为了结束某个用户的连接或者关闭服务器，ReaderThread 改写了interrupt 方法,使其既能处理标准的中断，也能关闭底层的套接字。
 * 因此，无论ReaderThread线程是在read方法中阻塞还是在某个可中断的阻塞方法中阻塞，都可以被中断并停止执行当前的工作。
 *
 * @author wulang
 * @create 2020/1/13/21:43
 */
public class ReaderThread extends Thread {
    private final Socket socket;
    private final InputStream in;
    private int BUFSZ;

    public ReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignored) {

        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[BUFSZ];
            while (true) {
                int count = in.read(buf);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    processBuffer(buf, count);
                }
            }
        } catch (IOException e) {
            //允许线程退出
        }
    }

    private void processBuffer(byte[] buf, int count) {

    }
}
