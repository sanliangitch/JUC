package fenpai;

import java.io.Serializable;

/**
 * 重载方法匹配优先级
 * 按照 char > int > long > float > double 的顺序,不会匹配到byte和short类型的重载
 * 有一些单个参数大中能成立的自动转型,在变长参数中是不成立的
 *
 * @author wulang
 * @create 2021/1/3/15:08
 */
public class Overload {
    public static void sayHello(Object arg) {
        System.out.println("hello Object");
    }

    public static void sayHello(int... arg) {
        System.out.println("hello int");
    }

    public static void sayHello(long arg) {
        System.out.println("hello arg");
    }

    public static void sayHello(Character arg) {
        System.out.println("hello Character");
    }

    public static void sayHello(char arg) {
        System.out.println("hello char");
    }

    public static void sayHello(char... arg) {
        System.out.println("hello char...");
    }

    public static void sayHello(Serializable arg) {
        System.out.println("hello Serializable");
    }

    public static void sayHello(Comparable arg) {
        System.out.println("hello Serializable");
    }

    public static void main(String[] args) {
        sayHello('a');
    }
}
