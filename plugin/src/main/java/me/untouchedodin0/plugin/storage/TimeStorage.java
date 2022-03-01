package me.untouchedodin0.plugin.storage;

import java.util.ArrayList;
import java.util.List;

public class TimeStorage {

    public List<Long> times = new ArrayList<>();

    public void addTime(long time) {
        times.add(time);
    }

    public List<Long> getTimes() {
        return times;
    }
}
