package designPattern.composite;

public class Test {
    public static void main(String[] args) {
        Component head=new Composite("总部");
        Component projectPart=new Composite("项目部");
        Component finance=new Composite("财务部");

        Component leaf1=new Composite("刘会计");
        Component leaf2=new Composite("张出纳");

        Component leaf3=new Composite("王二麻子工程师");
        Component leaf4=new Composite("李二狗子测试");
        Component leaf5=new Composite("赵扒皮产品");


        head.add(projectPart,finance);
        projectPart.add(leaf1,leaf2);
        finance.add(leaf3,leaf4,leaf5);

        head.display(0);

    }
}
