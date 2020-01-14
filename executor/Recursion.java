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
    void processSequentially(List<Element> elements) {
        for (Element e : elements) {
            process(e);
        }
    }

    /**
     * 将串行执行转换为并行执行
     *
     * @param exec
     * @param elements
     */
    void processInParallel(Executor exec, List<Element> elements) {
        for (final Element e : elements) {
            exec.execute(() -> process(e));
        }
    }

    private void process(Element e) {

    }

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