package hr.fer.hmo.checker;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.ArrayList;
import java.util.Arrays;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + destComp;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + srcComp;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Route other = (Route) obj;
        if (destComp != other.destComp) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        if (srcComp != other.srcComp) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "<" + srcComp + "," + destComp + "," + Arrays.toString(path.stream().toArray()).replaceAll(" ", "")
                + ">";
    }

}
