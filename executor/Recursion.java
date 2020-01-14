package executor;

import javax.swing.text.Element;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * 递归算法的并行化
 *
 * @author wulang
 * @create 2020/1/14/17:37
 */
public class Recursion {
    /**
     * 如果循环中的迭代操作都是独立的，并且不需要等待所有的迭代操作都完成再继续执行，
     * 那么就可以使用Executor将串行循环转化为并行循环。
     * 在程序中 processSequentially 和 processInParallel 中给出了这种方法。
     *
     * @param elements
     */
    void processSequentially(List<Element> elements) {
        for (Element e : elements) {
            process(e);
        }
    }

    /**
     * 将串行执行转换为并行执行
     * <p>
     * 调用processInParallel比调用processSequentially能更快地返回，
     * 因为processInParallel会在所有下载任务都进入了Executor 的队列后就立即返回，
     * 而不会等待这些任务全部完成。如果需要提交一个任务集并等待它们完成，
     * 那么可以使用ExecutorService.invokeAll,并且在所有任务都执行完成后调用CompletionService来获取结果
     *
     * @param exec
     * @param elements
     */
    void processInParallel(Executor exec, List<Element> elements) {
        for (final Element e : elements) {
            exec.execute(() -> process(e));
        }
    }

    /**
     * 当串行循环中的各个迭代操作之间彼此独立，并且每个迭代操作执行的工作量比管理一个新任务时带来的开销更多，
     * 那么这个串行循环就适合并行化。
     *
     * @param e
     */
    private void process(Element e) {

    }

    /**
     * 在一些递归设计中同样可以采用循环并行化的方法。在递归算法中通常都会存在串行循环，
     * 而且这些循环可以按照程序上面两个方式进行并行化。一种简单的情况是:在每个迭代操作中都不需要来自
     * 于后续递归迭代的结果。例如，程序的sequentialRecursive用深度优先算法遍历一棵树，
     * 在每个节点上执行计算并将结果放入一个集合。修改后的parallelRecursive同样执行深度优先遍历，
     * 但它并不是在访问节点时进行计算，而是为每个节点提交一个任务来完成计算。
     *
     * @param nodes
     * @param results
     * @param <T>
     */
    public <T> void sequentialRecursive(List<Node> nodes,
                                        Collection<T> results) {
        for (Node n : nodes) {
            results.add(n.compute());
            sequentialRecursive(n.getChildren(), results);
        }
    }

    /**
     * 将串行递归转换为并行递归
     *
     * @param exec
     * @param nodes
     * @param results
     * @param <T>
     */
    public <T> void parallelRecursive(final Executor exec,
                                      List<Node> nodes,
                                      final Collection<T> results) {
        for (final Node n : nodes) {
            exec.execute(() -> results.add(n.compute()));
        }
    }

    /**
     * 等待通过并行方式计算的结果
     * <p>
     * 当parallelRecursive返回时，树中的各个节点都已经访问过了( 但是遍历过程仍然是串行的，只有compute调用才是并行执行的)，
     * 并且每个节点的计算任务也已经放入Executor 的工作队列。parallelRecursive 的调用者可以通过以下方式等待所有的结果:
     * 创建一个特定于遍历过程的Executor,并使用shutdown和awaitTermination等方法，如程序 getParallelResults 所示。
     *
     * @param nodes
     * @param <T>
     * @return
     * @throws InterruptedException
     */
    public <T> Collection<T> getParallelResults(List<Node> nodes) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        Queue<T> resultQueue = new ConcurrentLinkedDeque<T>();
        parallelRecursive(exec, nodes, resultQueue);
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return resultQueue;
    }
}

class Node {
    public <T> T compute() {
        return null;
    }

    public List<Node> getChildren() {
        return null;
    }
}