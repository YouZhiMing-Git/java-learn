package designPattern.iterator;

public class MyListIterator<E> implements Iterator<E>{
    private MyList myList;

   public MyListIterator(MyList list){
       this.myList=list;
   }


    @Override
    public E  next() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }
}
