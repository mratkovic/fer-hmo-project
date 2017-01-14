package hr.fer.hmo.checker;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.ArrayList;

public class Route {
    int srcComp;
    int destComp;
    ArrayList<Integer> path;

    public Route(final int src, final int dest, final ArrayList<Integer> path) {
        this.srcComp = src;
        this.destComp = dest;
        this.path = path;
    }

    public int getSrcComp() {
        return this.srcComp;
    }

    public int getDestComp() {
        return this.destComp;
    }

    public ArrayList<Integer> getPath() {
        return this.path;
    }
}
