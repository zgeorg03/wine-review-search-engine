package com.zgeorg03.models;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Posting List refers to a particular term
 */
public class PostingList implements Serializable{

    /** DocumentID -> Positions **/
    private int frequency;
    private final TreeMap<Integer, PositionsEntry> documents;

    public PostingList() {
        this.documents = new TreeMap<>();
    }


    /**
     *
     */
    public void addDocumentRef(int documentID, int position){
        frequency++;

        PositionsEntry postingEntry = documents.getOrDefault(documentID,new PositionsEntry());

        postingEntry.addPosition(position);

        documents.putIfAbsent(documentID,postingEntry);

    }

    public TreeMap<Integer, PositionsEntry> getDocuments() {
        return documents;
    }

    public void decreaseFreqBy(int size) {
        frequency-=size;
    }
    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "PostingList{" +
                "frequency=" + frequency +
                ", documents=" + documents +
                '}';
    }

}
