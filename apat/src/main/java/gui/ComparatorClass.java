package gui;

import parser.dex.DexClass;

import java.util.Comparator;

/**
 * Created by acgmohu on 14-6-23.
 * 比较两个 DexClass 对象（排序）。
 */
public class ComparatorClass implements Comparator<DexClass> {
    @Override
    public int compare(DexClass arg0, DexClass arg1) {
        final String name0 = arg0.className;
        final String name1 = arg1.className;

        if (name0.contains("/")) {
            if (name1.contains("/")) {
                return name0.compareTo(name1);
            } else {
                return "".compareTo(name0);
            }
        }

        if (name1.contains("/")) {
            return name1.compareTo("");
        } else {
            return name0.compareTo(name1);
        }
    }

}