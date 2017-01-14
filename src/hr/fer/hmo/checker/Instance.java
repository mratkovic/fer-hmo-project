package hr.fer.hmo.checker;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class Instance {
    public HashMap<Integer, Float> cpuDemands = new HashMap<>();
    public HashMap<Integer, Float> memDemands = new HashMap<>();
    public HashMap<Integer, Float> cpuAvailable = new HashMap<>();
    public HashMap<Integer, Float> memAvailable = new HashMap<>();
    public HashMap<Integer, Float> maxLatency = new HashMap<>();
    public HashMap<Integer, Float> nodePower = new HashMap<>();
    public HashMap<Integer, Float> serverIdlePower = new HashMap<>();
    public HashMap<Integer, Float> serverMaxPower = new HashMap<>();

    public HashMap<Integer, Integer> serverAllocation = new HashMap<>();
    public HashMap<Integer, ArrayList<Integer>> serviceChains = new HashMap<>();
    public ArrayList<Link> links = new ArrayList<>();
    public ArrayList<LinkDemand> linkDemands = new ArrayList<>();

    public HashMap<Integer, Float> cpuPerNode = new HashMap<>();
    public HashMap<Integer, Float> memPerNode = new HashMap<>();
    public HashMap<Integer, ArrayList<Integer>> onNode = new HashMap<>();
    public ArrayList<Integer> usedNodes;
    public ArrayList<Integer> usedComponents;
    public HashMap<Integer, Float> serverMaxCost = new HashMap<>();

    public HashMap<Pair<Integer, Integer>, ArrayList<Path>> paths = new HashMap<>();
    public HashMap<Integer, HashMap<Integer, Link>> linksFrom = new HashMap<>();

    public HashMap<Pair<Integer, Integer>, LinkDemand> demanded = new HashMap<>();

    public int nServers;
    public int nVms;
    public int nRes;
    public int nNodes;
    public int nServiceChains;

    public float totalCpuAskedFor;
    public float totalMemAskedFor;

    public Instance(final String path) throws IOException {
        read(path);
        calcNodeResources();
        calcDemands();
        calcLinksFrom();
        HashSet<Integer> hashUsed = new HashSet<>(serverAllocation.values());
        usedNodes = new ArrayList<>(hashUsed);

        hashUsed = new HashSet<>();
        for (ArrayList<Integer> chain : serviceChains.values()) {
            hashUsed.addAll(chain);
        }
        usedComponents = new ArrayList<>(hashUsed);
        calcPaths();

        for (LinkDemand d : linkDemands) {
            int src = d.srcComp;
            int dest = d.destComp;
            demanded.put(new Pair<>(src, dest), d);
        }

        for (ArrayList<Integer> servers : onNode.values()) {
            servers.sort(new Comparator<Integer>() {

                @Override
                public int compare(final Integer o1, final Integer o2) {
                    return Float.compare(serverMaxCost.get(o1), serverMaxCost.get(o2));
                }

            });

        }
        usedNodes.sort(new Comparator<Integer>() {

            @Override
            public int compare(final Integer o1, final Integer o2) {
                return -Float.compare(cpuPerNode.get(o1), cpuPerNode.get(o2));
            }
        });

        usedComponents.sort((i, j) -> Integer.compare(i, j));

    }

    private void calcLinksFrom() {
        for (int i = 0; i <= nNodes; ++i) {
            linksFrom.put(i, new HashMap<>());
        }

        for (Link l : links) {
            linksFrom.get(l.srcNode).put(l.destNode, l);
        }

    }

    private void calcPaths() {
        for (int start : usedNodes) {
            Path p = new Path();
            p.nodes.add(start);
            dfs(start, 7, p);
        }

        for (ArrayList<Path> ps : paths.values()) {

            ps.sort(new Comparator<Path>() {

                @Override
                public int compare(final Path o1, final Path o2) {
                    int o = Float.compare(o1.totalCost, o2.totalCost);
                    if (o != 0) {
                        return o;
                    }
                    o = Float.compare(o1.capacity, o2.capacity);
                    if (o != 0) {
                        return -o;
                    }
                    return Integer.compare(o1.nodes.size(), o2.nodes.size());
                }
            });
        }
    }

    private void dfs(final int start, final int allowedDepth, final Path p) {
        if (allowedDepth == 0) {
            return;
        }
        for (int e : linksFrom.get(start).keySet()) {
            if (p.nodes.contains(e)) {
                continue;
            }
            p.nodes.add(e);

            if (usedNodes.contains(e)) {
                Path path = new Path(p.nodes);
                int s = p.nodes.get(0);
                Pair<Integer, Integer> pair = new Pair<>(s, e);
                if (!paths.containsKey(pair)) {
                    paths.put(pair, new ArrayList<>());
                }
                path.calcPathParams(this);
                paths.get(pair).add(path);
            }
            dfs(e, allowedDepth - 1, p);
            p.nodes.remove(p.nodes.size() - 1);
        }

    }

    private void calcDemands() {
        for (Entry<Integer, Float> pair : cpuDemands.entrySet()) {
            totalCpuAskedFor += pair.getValue();
        }

        for (Entry<Integer, Float> pair : memDemands.entrySet()) {
            totalMemAskedFor += pair.getValue();
        }

    }

    public void calcNodeResources() {
        for (int i = 0; i <= nNodes; ++i) {
            cpuPerNode.put(i, Float.valueOf(0));
            memPerNode.put(i, Float.valueOf(0));
            onNode.put(i, new ArrayList<>());
        }

        for (Entry<Integer, Integer> pair : serverAllocation.entrySet()) {
            int server = pair.getKey();
            int node = pair.getValue();
            float cpu = cpuAvailable.get(server);
            float mem = memAvailable.get(server);
            cpuPerNode.put(node, cpu + cpuPerNode.get(node));
            memPerNode.put(node, mem + memPerNode.get(node));
            onNode.get(node).add(server);
        }
        for (int i = 1; i <= nServers; ++i) {
            serverMaxCost.put(i, calcServerMaxCost(i));
        }

        for (int i = 0; i <= nNodes; ++i) {
            onNode.get(i).sort(serverComparator);
        }
    }

    public float calcServerMaxCost(final Integer server) {
        float cost = (serverMaxPower.get(server).floatValue() - serverIdlePower.get(server).floatValue())
                / cpuAvailable.get(server).floatValue();
        return cost;
    }

    Comparator<Integer> serverComparator = new Comparator<Integer>() {
        @Override
        public int compare(final Integer s1, final Integer s2) {
            return Float.compare(serverMaxCost.get(s2), serverMaxCost.get(s1));
        }

    };

    public static int parseVar(final String line) {
        String end = line.split("=")[1].trim();
        String num = end.substring(0, end.length() - 1);
        return Integer.parseInt(num);
    }

    public void read(final String pathToFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(pathToFile)));
        String line = "";

        while ((line = br.readLine()) != null) {
            String field = line.split("=")[0].trim();
            switch (field) {

            case "numServers":
                nServers = parseVar(line);
                break;

            case "numVms":
                nVms = parseVar(line);
                break;

            case "numRes":
                nRes = parseVar(line);
                break;

            case "numNodes":
                nNodes = parseVar(line);
                break;

            case "numServiceChains":
                nServiceChains = parseVar(line);
                break;

            case "VmDemands":
                this.readLinkDemands(br);
                break;
            case "P":
                this.readNodePower(br);
                break;
            case "al":
                this.readServerAllocation(br);
                break;

            case "av":
                this.readAvailableResources(br);
                break;
            case "sc":
                this.readServiceChains(br);
                break;
            case "lat":
                this.readLatencyConstraints(br);
                break;
            case "req":
                this.readResourceDemands(br);
                break;
            case "Edges":
                this.readLinks(br);
                break;
            case "P_max":
                this.readServerMaxPower(br);
                break;
            case "P_min":
                this.readserverIdlePower(br);
            }
        }

    }

    private void readserverIdlePower(final BufferedReader br) throws IOException {
        String line = br.readLine().replace("[", "").replace("]", "").replace(";", "");
        String[] pow = line.split(",");

        for (int i = 0; i < pow.length; ++i) {
            this.serverIdlePower.put(Integer.valueOf(i + 1), Float.valueOf(Float.parseFloat(pow[i])));
        }

    }

    private void readServerMaxPower(final BufferedReader br) throws IOException {
        String line = br.readLine().replace("[", "").replace("]", "").replace(";", "");
        String[] pow = line.split(",");

        for (int i = 0; i < pow.length; ++i) {
            this.serverMaxPower.put(Integer.valueOf(i + 1), Float.valueOf(Float.parseFloat(pow[i])));
        }

    }

    private void readNodePower(final BufferedReader br) throws IOException {
        String line = br.readLine().replace("[", "").replace("]", "").replace(";", "");
        String[] pow = line.split(",");

        for (int i = 0; i < pow.length; ++i) {
            this.nodePower.put(Integer.valueOf(i + 1), Float.valueOf(Float.parseFloat(pow[i])));
        }

    }

    private void readServerAllocation(final BufferedReader br) throws IOException {
        new String();
        int lineNum = 0;

        String line;
        while (!(line = br.readLine().replace("[", "").replace("]", "")).contains(";")) {
            ++lineNum;
            String[] sa = line.split(",");

            for (int i = 0; i < sa.length; ++i) {
                if (Integer.parseInt(sa[i]) == 1) {
                    this.serverAllocation.put(Integer.valueOf(lineNum), Integer.valueOf(i + 1));
                }
            }
        }

    }

    private void readServiceChains(final BufferedReader br) throws IOException {
        new String();
        int chainNum = 0;

        String line;
        while (!(line = br.readLine().replace("[", "").replace("]", "")).contains(";")) {
            ++chainNum;
            String[] sc = line.split(",");
            ArrayList<Integer> chain = new ArrayList<>();

            for (int i = 0; i < sc.length; ++i) {
                if (Integer.parseInt(sc[i]) == 1) {
                    chain.add(Integer.valueOf(i + 1));
                }
            }

            this.serviceChains.put(Integer.valueOf(chainNum), chain);
        }

    }

    private void readLatencyConstraints(final BufferedReader br) throws IOException {
        String line = br.readLine().replace("[", "").replace("]", "").replace(";", "");
        String[] lat = line.split(",");

        for (int i = 0; i < lat.length; ++i) {
            this.maxLatency.put(Integer.valueOf(i + 1), Float.valueOf(Float.parseFloat(lat[i])));
        }

    }

    private void readLinkDemands(final BufferedReader br) throws NumberFormatException, IOException {
        new String();

        String line;
        while (!(line = br.readLine().replace("<", "").replace(">", "")).contains(";")) {
            String[] newDemand = line.split(",");
            int src = Integer.parseInt(newDemand[0]);
            int dest = Integer.parseInt(newDemand[1]);
            float traffic = Float.parseFloat(newDemand[2]);
            this.linkDemands.add(new LinkDemand(src, dest, traffic));
        }

    }

    private void readLinks(final BufferedReader br) throws IOException {
        new String();

        String line;
        while (!(line = br.readLine().replace("<", "").replace(">", "")).contains(";")) {
            String[] newLink = line.split(",");
            int src = Integer.parseInt(newLink[0]);
            int dest = Integer.parseInt(newLink[1]);
            float cap = Float.parseFloat(newLink[2]);
            float pow = Float.parseFloat(newLink[3]);
            float lat = Float.parseFloat(newLink[4]);
            this.links.add(new Link(src, dest, cap, pow, lat));
        }

    }

    private void readAvailableResources(final BufferedReader br) throws IOException {
        String line = br.readLine().replace("[", "").replace("]", "");
        String[] avCpu = line.split(",");

        for (int avMem = 0; avMem < avCpu.length; ++avMem) {
            this.cpuAvailable.put(Integer.valueOf(avMem + 1), Float.valueOf(Float.parseFloat(avCpu[avMem])));
        }

        line = br.readLine().replace("[", "").replace("]", "").replace(";", "");
        String[] var6 = line.split(",");

        for (int i = 0; i < var6.length; ++i) {
            this.memAvailable.put(Integer.valueOf(i + 1), Float.valueOf(Float.parseFloat(var6[i])));
        }

    }

    private void readResourceDemands(final BufferedReader br) throws IOException {
        String line = br.readLine().replace("[", "").replace("]", "");
        String[] cpu = line.split(",");

        for (int mem = 0; mem < cpu.length; ++mem) {
            this.cpuDemands.put(Integer.valueOf(mem + 1), Float.valueOf(Float.parseFloat(cpu[mem])));
        }

        line = br.readLine().replace("[", "").replace("]", "").replace(";", "");
        String[] var6 = line.split(",");

        for (int i = 0; i < var6.length; ++i) {
            this.memDemands.put(Integer.valueOf(i + 1), Float.valueOf(Float.parseFloat(var6[i])));
        }

    }

    public void resetTmp() {
        for (Link l : links) {
            l.reset();
        }

    }
}
