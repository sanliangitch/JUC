package executor.puzzle;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在解决器中找不到答案
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
