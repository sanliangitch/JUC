package executor.puzzle;

import java.util.LinkedList;
import java.util.List;

/**
 * 用于谜题解决框架的链表节点
 *
 * @author wulang
 * @create 2020/1/14/17:58
 */
class Node<P,M> {
    final P pos;
    final M move;
    final Node<P,M> prev;
    Node(P pos,M move,Node<P,M> prev){
        this.pos = pos;
        this.move = move;
        this.prev = prev;
    }
    List<M> asMoveList(){
        List<M> solution = new LinkedList<M>();
        for (Node<P,M> n = this;n.move != null; n = n.prev){
            solution.add(0,n.move);
        }
        return solution;
    }
}
