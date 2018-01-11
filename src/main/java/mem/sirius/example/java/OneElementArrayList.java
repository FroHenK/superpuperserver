package mem.sirius.example.java;

import java.util.ArrayList;


public class OneElementArrayList<E> extends ArrayList<E> {
    public OneElementArrayList(E element) {
        super();
        this.add(element);
    }
}
