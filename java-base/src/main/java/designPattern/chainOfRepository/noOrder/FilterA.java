package designPattern.chainOfRepository.noOrder;


public class FilterA implements Filter {
    @Override
    public void doFilter(int request) {
        System.out.println("当前为" + request);
    }
}
