package designPattern.strategy;

public class CountContent {
    private Count count;
    public CountContent(Count count){
        this.count=count;
    }
    public int getResult(int param){
        return count.getResult(param);
    }
}
