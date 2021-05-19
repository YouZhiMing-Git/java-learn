package designPattern.chainOfRepository.noOrder;

public class Test {
    public static void main(String[] args) {
        FilterChain filterChain = new FilterChain();
        filterChain.add(new FilterA())
                .add(new FilterB())
                .add(new FilterC());

        filterChain.doFilter(11);
    }
}
