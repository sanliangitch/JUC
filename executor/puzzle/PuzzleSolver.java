package executor.puzzle;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在解决器中找不到答案
 * <p>
 * 找到解答的时间可能比等待的时间要长，因此在解决器中需要包含几个结束条件。
 * 其中一个结束条件是时间限制，这很容易实现:在ValueLatch中实现一个限时的getValue (其中将使用限时版本的await)，
 * 如果getValue超时，那么关闭Executor并声明出现了一个失败。
 * 另一个结束条件是某种特定于谜题的标准，例如只搜索特定数量的位置。此外，还可以提供一种取消机制，由用户自己决定何时停止搜索。
 *
 * @author wulang
 * @create 2020/1/14/20:08
 */
public class PuzzleSolver<P, M> extends ConcurrentPuzzleSolver<P, M> {
    private final AtomicInteger taskConut = new AtomicInteger(0);

    public PuzzleSolver(Puzzle<P, M> puzzle, ExecutorService exec, ConcurrentMap<P, Boolean> seen) {
        super(puzzle, exec, seen);
    }

    protected Runnable newTask(P p, M m, Node<P, M> n) {
        return new CountingSolveTask(p, m, n);
    }

    class CountingSolveTask extends SolverTask {
        CountingSolveTask(P pos, M move, Node<P, M> prev) {
            super(pos, move, prev);
            taskConut.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                if (taskConut.decrementAndGet() == 0) {
                    solution.setValue(null);
                }
            }
        }
    }
}
