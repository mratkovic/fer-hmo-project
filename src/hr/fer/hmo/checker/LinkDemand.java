package hr.fer.hmo.checker;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class LinkDemand {
    int srcComp;
    int destComp;
    float traffic;

    LinkDemand(final int srcComp, final int destComp, final float traffic) {
        this.srcComp = srcComp;
        this.destComp = destComp;
        this.traffic = traffic;
    }

    public int getSrcComp() {
        return this.srcComp;
    }

    public int getDestComp() {
        return this.destComp;
    }

    public float getTraffic() {
        return this.traffic;
    }
}
