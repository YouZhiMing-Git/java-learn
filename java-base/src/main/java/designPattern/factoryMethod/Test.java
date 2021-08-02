package designPattern.factoryMethod;

public class Test {
    public static void main(String[] args) {
        AnimalFactory animalFactory = new DogFactory();
        Animal animal = animalFactory.create();
        animal.eat();
    }
}
