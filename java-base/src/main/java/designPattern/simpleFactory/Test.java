package designPattern.simpleFactory;

public class Test {
    public static void main(String[] args) {
        AnimalFactory animalFactory = new AnimalFactory();
        Animal animal=animalFactory.createAnimal("dog");
        animal.eat();
    }
}
