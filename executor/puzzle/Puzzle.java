package executor.puzzle;

import java.util.Set;

/**
 * 表示"搬箱子"之类谜题的抽象类
 *
 * @author wulang
 * @create 2020/1/14/17:55
 */
public interface Puzzle<P,M> {
    P initialPosition();
    boolean isGoal(P position);
    Set<M> legalMoves(P position);
    P move(P position,M move);
}
