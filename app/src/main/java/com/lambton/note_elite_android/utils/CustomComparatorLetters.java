package com.lambton.note_elite_android.utils;

import java.util.Comparator;

public class CustomComparatorLetters implements Comparator<CustomItem> {

    @Override
    public int compare (CustomItem o1, CustomItem o2) {
        return o1.getLetter().compareTo(o2.getLetter());
    }
}