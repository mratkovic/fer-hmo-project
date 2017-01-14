package hr.fer.hmo.checker;
//

// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class Link {
    public int srcNode;
    public int destNode;
    public float capacity;
    public float powerConsumption;
    public float latency;
    public float traffic;
    public boolean active;

    public Link(final int srcNode, final int destNode, final float capacity, final float powerConsumption,
            final float latency) {
        this.srcNode = srcNode;
        this.destNode = destNode;
        this.capacity = capacity;
        this.powerConsumption = powerConsumption;
        this.latency = latency;
        this.traffic = 0.0F;
        this.active = false;
    }

    public Link(final int srcNode, final int destNode) {
        super();
        this.srcNode = srcNode;
        this.destNode = destNode;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + destNode;
        result = prime * result + srcNode;
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
        Link other = (Link) obj;
        if (destNode != other.destNode) {
            return false;
        }
        if (srcNode != other.srcNode) {
            return false;
        }
        return true;
    }

}
