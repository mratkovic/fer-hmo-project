package hr.fer.zemris.optjava.dz4.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import hr.fer.hmo.checker.ConstraintsChecker;
import hr.fer.hmo.checker.FitnessCalculator;
import hr.fer.hmo.checker.Instance;
import hr.fer.hmo.checker.Pair;
import hr.fer.hmo.checker.Path;
import hr.fer.hmo.checker.Route;
import hr.fer.hmo.checker.Solution;

public class InstanceFunction implements IFunction {
    public Instance problem;
    public FitnessCalculator calc;
    public ConstraintsChecker checker;
    boolean verbose;

    public InstanceFunction(final Instance problem, final FitnessCalculator calc, final ConstraintsChecker checker) {
        this(problem, calc, checker, false);
    }

    public void print(final String msg) {
        if (verbose) {
            System.out.println(msg);
        }

    }

    public InstanceFunction(final Instance problem, final FitnessCalculator calc, final ConstraintsChecker checker,
            final boolean verbose) {
        super();
        this.problem = problem;
        this.calc = calc;
        this.checker = checker;
        this.verbose = verbose;
    }

    @Override
    public double valueAt(final int[] values) {
        Solution sol = findSolution(values);

        if (sol == null) {
            return Float.POSITIVE_INFINITY;
        }
        int penalty = checker.check(sol);
        if (penalty > 500) {
            return Float.POSITIVE_INFINITY;
        }

        return calc.calculate(sol) + penalty * 1000;

    }

    private Solution findSolution(final int[] values) {
        Solution sol = new Solution();
        if (values.length != problem.usedComponents.size()) {
            throw new IllegalArgumentException("Invalid chromosome size");
        }

        for (int i = 0; i < values.length; ++i) {
            sol.componentLocation.put(problem.usedComponents.get(i), values[i]);
        }

        HashMap<Pair<Integer, Integer>, Float> pathsNeeded = calcNeededPaths(sol);
        HashMap<Pair<Integer, Integer>, ArrayList<Integer>> foundPaths = findPaths(pathsNeeded);
        if (foundPaths == null) {
            return null;
        }
        assignPaths(sol, foundPaths);
        return sol;
    }

    private void assignPaths(final Solution sol, final HashMap<Pair<Integer, Integer>, ArrayList<Integer>> foundPaths) {
        HashSet<Pair<Integer, Integer>> seen = new HashSet<>();

        for (ArrayList<Integer> chain : problem.serviceChains.values()) {
            for (int i = 1; i < chain.size(); ++i) {
                int s = chain.get(i - 1);
                int e = chain.get(i);
                Pair<Integer, Integer> linkComps = new Pair<>(s, e);
                if (seen.contains(linkComps)) {
                    continue;
                }
                seen.add(linkComps);

                int server1 = sol.componentLocation.get(s);
                int server2 = sol.componentLocation.get(e);
                int n1 = problem.serverAllocation.get(server1);
                int n2 = problem.serverAllocation.get(server2);
                Pair<Integer, Integer> link = new Pair<>(n1, n2);
                if (n1 != n2) {
                    print(String.format("%d %d goes %s\n", s, e, foundPaths.get(link)));
                    sol.routes.add(new Route(s, e, foundPaths.get(link)));
                } else {
                    ArrayList<Integer> path = new ArrayList<>();
                    path.add(n1);
                    sol.routes.add(new Route(s, e, path));
                }
            }
        }
    }

    private HashMap<Pair<Integer, Integer>, Float> calcNeededPaths(final Solution sol) {
        HashMap<Pair<Integer, Integer>, Float> pathsNeeded = new HashMap<>();
        HashSet<Pair<Integer, Integer>> seen = new HashSet<>();

        for (ArrayList<Integer> chain : problem.serviceChains.values()) {
            print("\nChain" + chain);
            for (int i = 1; i < chain.size(); ++i) {
                int s = chain.get(i - 1);
                int e = chain.get(i);
                Pair<Integer, Integer> linkComps = new Pair<>(s, e);
                if (seen.contains(linkComps)) {
                    print(String.format("Seen %d to %d\n", s, e));
                    continue;
                }
                seen.add(linkComps);

                int server1 = sol.componentLocation.get(s);
                int server2 = sol.componentLocation.get(e);

                int n1 = problem.serverAllocation.get(server1);
                int n2 = problem.serverAllocation.get(server2);

                if (n1 != n2) {
                    Pair<Integer, Integer> linkNodes = new Pair<>(n1, n2);
                    if (!pathsNeeded.containsKey(linkNodes)) {
                        pathsNeeded.put(linkNodes, Float.valueOf(0));
                    }
                    float neededBefore = pathsNeeded.get(linkNodes);

                    float neededExtra = problem.demanded.get(linkComps).getTraffic();
                    print(String.format("%d to %d (%d %d) needed extra %f(total: %f)\n", n1, n2, s, e, neededExtra,
                            neededBefore + neededExtra));
                    pathsNeeded.put(linkNodes, neededBefore + neededExtra);

                } else {
                    Pair<Integer, Integer> linkNodes = new Pair<>(n1, n2);
                    if (!pathsNeeded.containsKey(linkNodes)) {
                        pathsNeeded.put(linkNodes, Float.valueOf(0));
                    }
                    float neededBefore = pathsNeeded.get(linkNodes);

                    print(String.format("%d to %d (%d %d) , same node (total:%f)\n", n1, n2, s, e, neededBefore));
                }
            }
        }
        return pathsNeeded;
    }

    private HashMap<Pair<Integer, Integer>, ArrayList<Integer>> findPaths(
            final HashMap<Pair<Integer, Integer>, Float> pathsNeeded) {
        HashSet<Integer> usedNodes = new HashSet<>();
        for (Pair<Integer, Integer> pair : pathsNeeded.keySet()) {
            int n1 = pair.first;
            int n2 = pair.second;
            usedNodes.add(n1);
            usedNodes.add(n2);
        }

        for (ArrayList<Path> ps : problem.paths.values()) {
            for (Path p : ps) {
                p.reduceCost(usedNodes, problem);
            }

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

            for (Path p : ps) {
                p.calcPathParams(problem);
            }
        }

        List<Pair<Integer, Integer>> pairs = new ArrayList<>(pathsNeeded.keySet());
        Collections.shuffle(pairs);
        HashMap<Pair<Integer, Integer>, ArrayList<Integer>> foundPaths = new HashMap<>();
        for (Pair<Integer, Integer> pair : pairs) {
            int s = pair.first;
            int e = pair.second;
            if (s == e) {
                continue;
            }
            float needed = pathsNeeded.get(pair);

            boolean found = false;
            for (Path p : problem.paths.get(pair)) {
                if (p.capacity >= needed) {
                    foundPaths.put(pair, p.nodes);
                    print(s + "," + e + " -- " + p.nodes);
                    found = true;
                    break;
                }
            }
            if (!found) {
                print("Not found " + s + " to " + e + " needed " + needed);
                return null;
            }
        }
        return foundPaths;
    }

    // TODO
    @Override
    public int numberOfVariables() {
        return problem.usedComponents.size();
    }

    @Override
    public void saveSolution(final int[] values, final String path) {
        Solution sol = findSolution(values);
        if (sol == null) {
            return;
        }
        try {
            sol.dumpToFile(path, problem);
        } catch (IOException e) {
            System.err.println("Could not save");
        }

    }
}
