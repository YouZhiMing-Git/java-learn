package designPattern.chainOfRepository.noOrder;


public class FilterB implements Filter {

    @Override
    public void doFilter(int request) {
        System.out.println("当前级别为" + request + ",处理结束");

    }
}
