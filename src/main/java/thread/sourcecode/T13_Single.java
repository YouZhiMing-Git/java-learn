package thread.sourcecode;

public class T13_Single {
    private volatile  static T13_Single instance;
    private  T13_Single(){

    }
    public static T13_Single getInstance() {
        if(instance==null){
            synchronized (T13_Single.class){
                if(instance==null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    instance=new T13_Single();
                }
            }
        }
        return instance;
    }
    public void m(){
        System.out.println("hello");
    }

    public static void main(String[] args) {
        for(int i=0;i<5;i++){
            new Thread(()->{
                T13_Single single=T13_Single.getInstance();
                single.m();
            }).start();
        }

    }

}
