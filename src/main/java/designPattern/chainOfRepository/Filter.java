package designPattern.chainOfRepository;

public abstract class Filter {
    Filter next;

    Filter setNext(Filter next) {
        this.next = next;
        return this;
    }
    abstract void doFilter(int request);
}
