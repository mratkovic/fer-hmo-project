package hr.fer.hmo.checker;
//

// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.HashMap;
import java.util.HashSet;

public class FitnessCalculator {
    Instance instance;

    public FitnessCalculator(final Instance instance) {
        this.instance = instance;
        // this.app = app;
    }

    public float calculate(final Solution s) {
        float fitness = 0.0F;
        fitness += calcServerPower(s);
        fitness += calcLinkAndNodePower(s);
        // this.app.textArea.append("Fitness: " + fitness);
        return fitness;
    }

    private float calcLinkAndNodePower(final Solution s) {
        float linkPower = 0.0f;
        float nodePower = 0.0f;
        for (Route route : s.getRoutes()) {

            for (int i = 0; i < route.getPath().size() - 1; ++i) {
                for (Link link : this.instance.links) {
                    if (link.getSrcNode() != route.getPath().get(i).intValue()
                            || link.getDestNode() != route.getPath().get(i + 1).intValue()) {
                        continue;
                    }
                    link.setActive(true);
                }
            }
        }
        HashSet<Integer> activeNodes = new HashSet<>();
        for (Link link : this.instance.links) {
            if (!link.isActive()) {
                continue;
            }
            linkPower += link.getPowerConsumption();
            activeNodes.add(link.getSrcNode());
            activeNodes.add(link.getDestNode());
        }
        for (Integer actNode : activeNodes) {
            nodePower += this.instance.nodePower.get(actNode).floatValue();
        }
        return linkPower + nodePower;
    }

    private float calcServerPower(final Solution s) {
        float serverPower = 0.0f;
        HashSet<Integer> activeServers = new HashSet<>();
        for (int comp : s.componentLocation.keySet()) {
            activeServers.add(s.componentLocation.get(comp));
        }

        for (int server : activeServers) {
            serverPower += instance.serverIdlePower.get(server).floatValue();
        }

        HashMap<Integer, Float> sumOfCpuDemands = new HashMap<>();

        for (int server : activeServers) {
            for (int comp : s.componentLocation.keySet()) {
                if (s.componentLocation.get(comp) != server) {
                    continue;
                }
                if (sumOfCpuDemands.containsKey(server)) {
                    float sumBefore = sumOfCpuDemands.get(server).floatValue();
                    sumOfCpuDemands.put(server,
                            Float.valueOf(sumBefore + this.instance.cpuDemands.get(comp).floatValue()));
                    continue;
                }
                sumOfCpuDemands.put(server, this.instance.cpuDemands.get(comp));
            }
            serverPower += (this.instance.serverMaxPower.get(server).floatValue()
                    - this.instance.serverIdlePower.get(server).floatValue()) * sumOfCpuDemands.get(server).floatValue()
                    / this.instance.cpuAvailable.get(server).floatValue();
        }
        return serverPower;
    }
}
