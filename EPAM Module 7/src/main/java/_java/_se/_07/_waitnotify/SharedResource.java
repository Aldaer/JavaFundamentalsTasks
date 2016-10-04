package _java._se._07._waitnotify;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class SharedResource {
    private List<Integer> list;

    public int numberOfActiveThreads = 0;
    public int numberOfWaitingThreads = 0;

    public SharedResource() {
        list = new ArrayList<Integer>();
    }

    public void setElement(Integer element) {
        list.add(element);
    }

    public Integer getElement() {
        if (list.size() > 0) {
            return list.remove(0);
        }
        return null;
    }

}
