package designPattern.chainOfRepository.noOrder;

import java.util.ArrayList;
import java.util.List;

public class FilterChain implements Filter{
    List<Filter> filters=new ArrayList<>();
    public FilterChain add(Filter filter){
        filters.add(filter);
        return this;
    }
    public void remove(Filter filter){
        filters.remove(filter);
    }

    @Override
    public void doFilter(int request) {
        filters.forEach(filter -> filter.doFilter(request));
    }


}
