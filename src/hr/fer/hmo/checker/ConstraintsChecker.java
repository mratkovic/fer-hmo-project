package hr.fer.hmo.checker;

import java.util.HashMap;

public class ConstraintsChecker {
    Instance instance;
    boolean verbose;

    public ConstraintsChecker(final Instance instance, final boolean verbose) {
        this.verbose = verbose;
        this.instance = instance;
    }

    public ConstraintsChecker(final Instance instance) {
        this.verbose = false;
        this.instance = instance;
    }

    public int check(final Solution s) {
        boolean valid = checkRoutesValid(s) && checkServerAllocation(s) && checkLatencyConstraints(s);
        if (!valid) {
            return 1000;
        }
        int serverWrong = checkResourceConstraints(s);
        int linkWrong = checkLinkConstraints(s);
        instance.resetTmp();
        return serverWrong + linkWrong;

    }

    private boolean checkServerAllocation(final Solution s) {
        for (int i = 0; i < 42; ++i) {
            if (!s.componentLocation.containsKey(Integer.valueOf(i + 1))) {
                return false;
            }
        }
        return true;
    }

    public void print(final String msg) {
        if (verbose) {
            System.out.println(msg);
        }

    }

    private boolean checkRoutesValid(final Solution s) {
        boolean found;

        for (int chain = 1; chain <= instance.serviceChains.size(); ++chain) {
            for (int j = 0; j < this.instance.serviceChains.get(chain).size() - 1; ++j) {
                found = false;
                for (Route route : s.getRoutes()) {
                    if (route.getSrcComp() != this.instance.serviceChains.get(chain).get(j).intValue()
                            || route.getDestComp() != this.instance.serviceChains.get(chain).get(j + 1).intValue()) {
                        continue;
                    }
                    found = true;
                    break;
                }
                if (!found) {
                    print("Route from component " + this.instance.serviceChains.get(chain).get(j) + " to component "
                            + this.instance.serviceChains.get(chain).get(j + 1) + " is missing!\n");
                    return false;
                }
            }
        }

        for (Route route : s.getRoutes()) {
            for (int i = 0; i < route.getPath().size() - 1; ++i) {
                found = false;
                for (Link link : this.instance.links) {
                    if (link.getSrcNode() != route.getPath().get(i).intValue()
                            || link.getDestNode() != route.getPath().get(i + 1).intValue()) {
                        continue;
                    }
                    found = true;
                }
                if (!found) {
                    print("Link from node " + route.getPath().get(i) + " to node " + route.getPath().get(i + 1)
                            + " does not exist!\n");
                    return false;
                }
            }
        }
        for (Route route2 : s.getRoutes()) {
            if (this.instance.serverAllocation.get(s.componentLocation.get(route2.getSrcComp())) != route2.getPath()
                    .get(0)) {
                print("Route from " + route2.getSrcComp() + " to " + route2.getDestComp()
                        + " starts with the node source component\n is not allocated to!\n");
                return false;
            }
            if (this.instance.serverAllocation.get(s.componentLocation.get(route2.getDestComp())) == route2.getPath()
                    .get(route2.getPath().size() - 1)) {
                continue;
            }
            print("Route from " + route2.getSrcComp() + "to " + route2.getDestComp()
                    + " ends with the node destination component\n is not allocated to!\n");
            return false;
        }
        return true;
    }

    private boolean checkLatencyConstraints(final Solution s) {
        HashMap<Integer, Float> chainLatency = new HashMap<>();

        for (int chain = 1; chain <= instance.serviceChains.size(); ++chain) {
            for (int j = 0; j < this.instance.serviceChains.get(chain).size() - 1; ++j) {
                for (Route route : s.getRoutes()) {
                    if (route.getSrcComp() != this.instance.serviceChains.get(chain).get(j).intValue()
                            || route.getDestComp() != this.instance.serviceChains.get(chain).get(j + 1).intValue()) {
                        continue;
                    }
                    for (int i = 0; i < route.getPath().size() - 1; ++i) {
                        for (Link link : this.instance.links) {
                            if (link.getSrcNode() != route.getPath().get(i).intValue()
                                    || link.getDestNode() != route.getPath().get(i + 1).intValue()) {
                                continue;
                            }
                            if (chainLatency.containsKey(chain)) {
                                float latencyBefore = chainLatency.get(chain).floatValue();
                                chainLatency.put(chain, Float.valueOf(latencyBefore + link.getLatency()));
                                continue;
                            }
                            chainLatency.put(chain, Float.valueOf(link.getLatency()));
                        }
                    }
                }
            }
        }

        for (int chn : chainLatency.keySet()) {
            // print("Chain: " + chn + " Latency: " +
            // chainLatency.get(chn));
            if (chainLatency.get(chn).floatValue() <= instance.maxLatency.get(chn).floatValue()) {
                continue;
            }
            print("Service chain " + chn + ": Total latency (" + chainLatency.get(chn) + ") > allowed latency ("
                    + this.instance.maxLatency.get(chn) + ") !\n");
            return false;
        }
        return true;
    }

    private int checkLinkConstraints(final Solution s) {
        for (Route route : s.getRoutes()) {

            for (int i = 0; i < route.getPath().size() - 1; ++i) {
                for (Link link : this.instance.links) {
                    if (link.getSrcNode() != route.getPath().get(i).intValue()
                            || link.getDestNode() != route.getPath().get(i + 1).intValue()) {
                        continue;
                    }
                    for (LinkDemand linkDemand : this.instance.linkDemands) {
                        if (linkDemand.getSrcComp() != route.getSrcComp()
                                || linkDemand.getDestComp() != route.getDestComp()) {
                            continue;
                        }
                        link.setTraffic(link.getTraffic() + linkDemand.getTraffic());
                    }
                }
            }
        }
        int wrongCnt = 0;
        for (Link link : this.instance.links) {
            print("Link from node " + link.getSrcNode() + " to node " + link.getDestNode() + " Link traffic: "
                    + link.getTraffic() + " link capacity: " + link.getCapacity());
            if (link.getTraffic() <= link.getCapacity()) {
                continue;
            }
            print("Link from node " + link.getSrcNode() + " to node " + link.getDestNode() + ": Traffic demand ("
                    + link.getTraffic() + ") > link capacity (" + link.getCapacity() + ") !\n");
            wrongCnt++;
        }
        return wrongCnt;
    }

    private int checkResourceConstraints(final Solution s) {
        HashMap<Integer, Float> cpuDemandPerServer = new HashMap<>();
        for (Integer keyServ : this.instance.cpuAvailable.keySet()) {
            float currentDemand = 0.0f;
            for (Integer keyComp : s.componentLocation.keySet()) {
                if (!s.componentLocation.get(keyComp).equals(new Integer(keyServ))) {
                    continue;
                }
                if (cpuDemandPerServer.containsKey(keyServ)) {
                    currentDemand = cpuDemandPerServer.get(keyServ).floatValue();
                }
                cpuDemandPerServer.put(keyServ,
                        Float.valueOf(Math
                                .round((currentDemand + this.instance.cpuDemands.get(keyComp).floatValue()) * 100.0f)
                                / 100.0f));
            }
        }
        int wrongCnt = 0;
        for (Integer key : cpuDemandPerServer.keySet()) {
            if (cpuDemandPerServer.get(key).floatValue() <= this.instance.cpuAvailable.get(key).floatValue()) {

                print("Server " + key + ": CPU demand (" + cpuDemandPerServer.get(key) + ") > available CPU ("
                        + this.instance.cpuAvailable.get(key) + ") !");
                continue;
            }
            wrongCnt++;
        }

        HashMap<Integer, Float> memDemandPerServer = new HashMap<>();
        for (Integer keyServ2 : this.instance.memAvailable.keySet()) {
            float currentDemand = 0.0f;
            for (Integer keyComp : s.componentLocation.keySet()) {
                if (!s.componentLocation.get(keyComp).equals(new Integer(keyServ2))) {
                    continue;
                }
                if (memDemandPerServer.containsKey(keyServ2)) {
                    currentDemand = memDemandPerServer.get(keyServ2).floatValue();
                }
                memDemandPerServer.put(keyServ2,
                        Float.valueOf(Math
                                .round((currentDemand + this.instance.memDemands.get(keyComp).floatValue()) * 100.0f)
                                / 100.0f));
            }
        }
        for (Integer key2 : memDemandPerServer.keySet()) {
            if (memDemandPerServer.get(key2).floatValue() <= this.instance.memAvailable.get(key2).floatValue()) {
                continue;
            }
            print("Server " + key2 + ": Memory demand (" + memDemandPerServer.get(key2) + ") > available memory ("
                    + this.instance.memAvailable.get(key2) + ") !\n");
            wrongCnt++;
        }
        return wrongCnt;
    }
}
