package designPattern.factoryMethod;

public class TigerFactory implements AnimalFactory{
    @Override
    public Animal create() {
        return new Tiger();
    }
}
