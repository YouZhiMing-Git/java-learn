package designPattern.composite;

import java.util.ArrayList;
import java.util.List;

public class Composite extends Component {
    List<Component> componentList;
    public Composite(String name) {
        super(name);
        componentList=new ArrayList<>(0);
    }

    public  void display(int depth) {
        for(int i=0;i<depth;i++){
            System.out.print("=");
        }
        System.out.println(name);
        for (Component component : componentList) {
            component.display(depth+1);
        }
    }

    @Override
    public void add(Component... components) {
        for (Component component : components) {
            componentList.add(component);
        }

    }

    @Override
    public void remove(Component component) {
        componentList.remove(component);
    }
}
