package com.ef.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for handling SQL batch operations.
 */
public class Batch<T> {

    private List<T> entries;

    public Batch() {
        this.entries = new ArrayList<>();
    }

    public void add(T entry) {
        entries.add(entry);
    }

    public void clear() {
        entries.clear();
    }

    public List<T> getEntries() {
        return entries;
    }

    public int size() {
        return entries.size();
    }

}
