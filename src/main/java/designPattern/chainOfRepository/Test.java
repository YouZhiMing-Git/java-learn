package designPattern.chainOfRepository;

public class Test {
    public static void main(String[] args) {
        Filter filterA=new FilterA();
        Filter filterB=new FilterB();
        Filter filterC=new FilterC();

        filterA.setNext(filterB.setNext(filterC));

        filterA.doFilter(2);
    }
}
