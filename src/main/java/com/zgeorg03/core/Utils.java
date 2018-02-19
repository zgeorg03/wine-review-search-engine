package com.zgeorg03.core;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class Utils {

    public static Set<Integer> intersectSortedSets(Set<Integer> a, Set<Integer> b){
        Set<Integer> result = new LinkedHashSet<>();
        Iterator<Integer> at = a.iterator();
        Iterator<Integer> bt = b.iterator();

        int l=-1,r=-1;
        while(at.hasNext() && bt.hasNext()){
            if(l<r){
                l = at.next();
            }else if(r<l){
                r = bt.next();
            }else{

                l = at.next();
                r = bt.next();
            }
            if(l==r)
                result.add(l);
        }
        return result;
    }
    public static Set<Integer> unionSortedSets(Set<Integer> a, Set<Integer> b){
        Set<Integer> result = new LinkedHashSet<>();
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    public static Set<Integer> diffSortedSets(Set<Integer> a, Set<Integer> b){
        Set<Integer> result = new LinkedHashSet<>();
        Iterator<Integer> at = a.iterator();

        if(a==null || a.isEmpty()){
            return result;
        }
        if(b==null || b.isEmpty()) {
            result.addAll(a);
            return result;
        }
        while(at.hasNext()){
            int value = at.next();
            if(!b.contains(value))
                result.add(value);
        }


        return result;
    }
}
