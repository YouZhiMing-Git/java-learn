package designPattern.iterator;

/***
 * 迭代器
 * 不同的迭代方式都需要实现该接口
 * @param <E>
 */
public interface Iterator<E> {
    E next();

    boolean hasNext();

}
