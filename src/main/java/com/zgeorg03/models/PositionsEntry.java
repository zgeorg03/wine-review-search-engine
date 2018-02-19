package com.zgeorg03.models;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class PositionsEntry implements Serializable {
    private final Set<Integer> positions;
    public PositionsEntry() {
        positions = new TreeSet<>();
    }

    /**
     * Add position in a tree set
     * @param position
     */
    public void addPosition(int position){
        this.positions.add(position);
    }

    /**
     * Return positions based on their natural ordering
     * @return
     */
    public Set<Integer> getPositions() {
        return positions;
    }

    @Override
    public String toString() {
        return "PostingEntry{" +
                ", positions=" + positions +
                '}';
    }

}
