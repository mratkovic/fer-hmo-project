package hr.fer.hmo.checker;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class Link {
    int srcNode;
    int destNode;
    float capacity;
    float powerConsumption;
    float latency;
    float traffic;
    boolean active;

    Link(final int srcNode, final int destNode, final float capacity, final float powerConsumption,
            final float latency) {
        this.srcNode = srcNode;
        this.destNode = destNode;
        this.capacity = capacity;
        this.powerConsumption = powerConsumption;
        this.latency = latency;
        this.traffic = 0.0F;
        this.active = false;
    }

    public int getSrcNode() {
        return this.srcNode;
    }

    public int getDestNode() {
        return this.destNode;
    }

    public float getCapacity() {
        return this.capacity;
    }

    public float getPowerConsumption() {
        return this.powerConsumption;
    }

    public float getLatency() {
        return this.latency;
    }

    public float getTraffic() {
        return this.traffic;
    }

    public void setTraffic(final float traffic) {
        this.traffic = traffic;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }
}
