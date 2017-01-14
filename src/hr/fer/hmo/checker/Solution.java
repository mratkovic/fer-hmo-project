package hr.fer.hmo.checker;
//

// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    Map<Integer, Integer> componentLocation = new HashMap();
    ArrayList<Route> routes = new ArrayList();

    public Solution() {
    }

    public Solution(final String path) throws IOException {
        read(path);
    }

    public void read(final String pathToFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathToFile));
        String line = br.readLine();
        int componentNum = 0;

        int src;
        while (!(line = br.readLine()).contains(";")) {
            ++componentNum;
            String[] components = line.replace("[", "").replace("]", "").split(",");

            for (src = 0; src < components.length; ++src) {
                if (components[src].equals("1")) {
                    this.componentLocation.put(Integer.valueOf(componentNum), Integer.valueOf(src + 1));
                }
            }
        }

        line = br.readLine();
        line = br.readLine();

        while (!(line = br.readLine().trim()).contains(";")) {
            String var12 = line.split("\\[")[0].replace("<", "");
            src = Integer.parseInt(var12.split(",")[0]);
            int dest = Integer.parseInt(var12.split(",")[1]);
            String nodes = line.split("\\[")[1].split("\\]")[0];
            ArrayList path = new ArrayList();
            String[] splitNodes = nodes.split(",");

            for (String splitNode : splitNodes) {
                path.add(Integer.valueOf(Integer.parseInt(splitNode)));
            }

            this.routes.add(new Route(src, dest, path));
        }

        br.close();
    }

    public Map<Integer, Integer> getComponentLocation() {
        return this.componentLocation;
    }

    public ArrayList<Route> getRoutes() {
        return this.routes;
    }
}
