package designPattern.chainOfRepository;



public class FilterB extends Filter {

    @Override
    public void doFilter(int request) {
        if(request<9){
            System.out.println("当前级别为" + request+",处理结束");
        }else {
            System.out.println("搞不定");
            next.doFilter(request);
        }
    }
}
