package executor.puzzle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 串行的谜题解答器
 * <p>
 * SequentialPuzzleSolver中给出了谜题框架的串行解决方案，它在谜题空间中执行一个深度优先搜索，当找到解答方案(不一定是最短的解决方案)
 * 后结束搜索。
 * <p>
 * 不足：
 * 通过修改解决方案以利用并发性，可以以并行方式来计算下一步移动以及目标条件，
 * 因为计算某次移动的过程在很大程度上与计算其他移动的过程是相互独立的。(之所以说“在很大程度上”，是因为在各个任务之间会共享一些可变状态，
 * 例如已遍历位置的集合。)如果有多个处理器可用，那么这将减少寻找解决方案所花费的时间。
 *
 * @author wulang
 * @create 2020/1/14/19:42
 */
public class SequentialPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final Set<P> seen = new HashSet<>();

    public SequentialPuzzleSolver(Puzzle<P, M> pmPuzzle) {
        this.puzzle = pmPuzzle;
    }

    public List<M> solve() {
        P pos = puzzle.initialPosition();
        return search(new Node<P, M>(pos, null, null));
    }

    private List<M> search(Node<P, M> node) {
        if (!seen.contains(node.pos)) {
            seen.add(node.pos);
            if (puzzle.isGoal(node.pos)) {
                return node.asMoveList();
            }
            for (M move : puzzle.legalMoves(node.pos)) {
                P pos = puzzle.move(node.pos, move);
                Node<P, M> child = new Node<P, M>(pos, move, node);
                List<M> reult = search(child);
                if (reult != null) {
                    return reult;
                }
            }
        }
        return null;
    }
}
