package algorithm;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 使用 Treiber 算法构造的非阻塞栈
 *
 * @author wulang
 * @create 2020/1/17/11:14
 */
public class ConcurrentStack<E> {

    AtomicReference<Node<E>> top = new AtomicReference<Node<E>>();

    public void push(E item) {
        Node<E> newHead = new Node<>(item);
        Node<E> oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead, newHead));
    }

    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = top.get();
            if (oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

    private static class Node<E> {
        volatile E item;
        volatile Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }
}
