package org.example.dsa_project.dsa;

import org.example.dsa_project.model.Emergency;
import java.util.ArrayList;
import java.util.List;

public class EmergencyMaxHeap {
    private final List<Emergency> heap = new ArrayList<>();
    public boolean isEmpty() {
        return heap.isEmpty();
    }
    public int size() {
        return heap.size();
    }
    public void add(Emergency e) {
        heap.add(e);
        siftUp(heap.size()-1);
    }
    public Emergency peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }
    public Emergency poll() {
        if (heap.isEmpty())
            return null;
        Emergency top = heap.get(0), last = heap.remove(heap.size()-1);
        if (!heap.isEmpty()) {
            heap.set(0,last);
            siftDown(0);
        }
        return top;
    }
    public List<Emergency> snapshot() {
        List<Emergency> copy = new ArrayList<>(heap);
        copy.sort(this::compare); return copy;
    }
    private int compare(Emergency a, Emergency b) {
        int c = Integer.compare(a.priorityScore(), b.priorityScore());
        if (c != 0) return c;
        return -a.getReportedAt().compareTo(b.getReportedAt());
    }
    private void siftUp(int i) {
        while (i>0) {
            int p=(i-1)/2;
            if (compare(heap.get(i),heap.get(p))<=0)
                break;
            swap(i,p); i=p;
        }
    }
    private void siftDown(int i) {
        while (true) {
            int l=2*i+1,r=2*i+2,b=i;
            if(l<heap.size()&&compare(heap.get(l),heap.get(b))>0)
                b=l;
            if(r<heap.size()&&compare(heap.get(r),heap.get(b))>0)
                b=r;
            if(b==i)
                break;
            swap(i,b);i=b;
        }
    }
    private void swap(int a,int b) {
        Emergency t=heap.get(a);heap.set(a,heap.get(b));heap.set(b,t);
    }
}
