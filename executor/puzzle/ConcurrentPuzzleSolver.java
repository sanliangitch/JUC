package executor.puzzle;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * 并发的谜题解答题
 * <p>
 * ConcurrentPuzzleSolver中使用了一个内部类SolverTask,这个类扩展了Node并实现了Runnable。
 * 大多数工作都是在run方法中完成的:首先计算出下一步可能到达的所有位置，并去掉已经到达的位置，
 * 然后判断(这个任务或者其他某个任务)是否已经成功地完成，最后将尚未搜索过的位置提交给Executor.
 *
 * @author wulang
 * @create 2020/1/14/19:49
 */
public class ConcurrentPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final ExecutorService exec;
    private final ConcurrentMap<P, Boolean> seen;
    final ValueLatch<Node<P, M>> solution =
            new ValueLatch<Node<P, M>>();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle, ExecutorService exec, ConcurrentMap<P, Boolean> seen) {
        this.puzzle = puzzle;
        this.exec = exec;
        this.seen = seen;
    }

    /**
     * 不足：
     * 如果不存在解答，那么ConcurrentPuzzleSolver就不能很好地处理这种情况:如果已经遍历了所有的移动和位置都没有找到解答，
     * 那么在getSolution调用中将永远等待下去。当遍历了整个搜索空间时，串行版本的程序将结束，但要结束并发程序会更困难。
     * 其中一种方法是:记录活动任务的数量，当该值为零时将解答设置为null,如程序 PuzzleSolver 所示。
     *
     * @return
     * @throws InterruptedException
     */
    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            exec.execute(newTask(p, null, null));
            //阻塞直到找到解答
            Node<P, M> solnNode = solution.getValue();
            return (solnNode == null) ? null : solnNode.asMoveList();
        } finally {
            exec.shutdown();
        }
    }

    private Runnable newTask(P p, M m, Node<P, M> n) {
        return new SolverTask(p, m, n);
    }

    class SolverTask extends Node<P, M> implements Runnable {

        SolverTask(P pos, M move, Node<P, M> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {
            if (solution.isSet() ||
                    seen.putIfAbsent(pos, true) != null) {
                return; //已经找到了解答或者已经遍历了这个位置
            }
            if (puzzle.isGoal(pos)) {
                solution.setValue(this);
            } else {
                for (M m : puzzle.legalMoves(pos)) {
                    exec.execute(newTask(puzzle.move(pos, m), m, this));
                }
            }
        }
    }
}