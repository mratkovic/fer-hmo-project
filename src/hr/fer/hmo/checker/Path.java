package hr.fer.hmo.checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Path {
    public ArrayList<Integer> nodes;
    public HashSet<Integer> usedNodes;
    public float totalCost;

    public float capacity;

    public Path(final ArrayList<Integer> nodes) {
        super();
        this.nodes = new ArrayList<>(nodes);
    }

    public Path() {
        super();
        this.nodes = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
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
        Path other = (Path) obj;
        if (nodes == null) {
            if (other.nodes != null) {
                return false;
            }
        } else if (!nodes.equals(other.nodes)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + nodes + ", totalCost=" + totalCost + ", capacity=" + capacity + "]";
    }

    public void calcPathParams(final Instance problem) {
        totalCost = 0;
        capacity = 0;

        for (int i = 1; i < nodes.size(); ++i) {
            int s = nodes.get(i - 1);
            int e = nodes.get(i);

            totalCost += problem.linksFrom.get(s).get(e).powerConsumption;
            totalCost += problem.linksFrom.get(s).get(e).capacity;
        }
        usedNodes = new HashSet<>(nodes);
        for (Integer server : usedNodes) {
            totalCost += problem.serverIdlePower.get(server);

        }

    }

    public void reduceCost(final List<Integer> servers, final Instance problem) {

        for (Integer server : servers) {
            if (usedNodes.contains(server)) {
                totalCost -= problem.serverIdlePower.get(server);
            }

        }

    }

}
