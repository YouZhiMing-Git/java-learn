package designPattern.simpleFactory;

public class AnimalFactory {
    public Animal createAnimal(String name){
        switch (name){
            case "dog":
                return new Dog();
            case "tiger":
                return new Tiger();
            default:
                return null;
        }
    }
}
