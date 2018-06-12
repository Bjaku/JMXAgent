package model.monitor.bjaku.com;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MemoryFiller {
    public List<Integer> someData;

    public static final Integer SINGLE_ELEMENT_SIZE = 16 + 24;

    public MemoryFiller(Integer size){
        someData = new LinkedList<>();
        for(int i = 0; i < size; ++i){
            someData.add(new Integer(i + 1000));
        }
    }
}
