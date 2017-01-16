package hr.fer.hmo.demo;

import java.io.IOException;
import java.util.Map.Entry;

import hr.fer.hmo.checker.ConstraintsChecker;
import hr.fer.hmo.checker.FitnessCalculator;
import hr.fer.hmo.checker.Instance;
import hr.fer.hmo.checker.Solution;

public class Demo {
    /**
     * Dummy runner used to inspect solution and random things
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        String instancePath = "./instanca.txt";

        Instance problem = new Instance(instancePath);
        System.out.println("Parsed...\n");
        System.out.println("Chains:" + problem.serviceChains.size());
        System.out.println("Links:" + problem.links.size());

        String solPath = "./best.txt";
        Solution sol = new Solution(solPath);

        ConstraintsChecker checker = new ConstraintsChecker(problem, true);
        FitnessCalculator calc = new FitnessCalculator(problem);
        double fit = calc.calculate(sol);
        System.out.println(checker.check(sol));
        System.out.println(fit);

        System.out.println("\n\nUsed nodes" + problem.usedNodes);
        System.out.println("Number of components: " + problem.nVms);

        System.out.println("Cpu per node");
        for (Entry<Integer, Float> pair : problem.cpuPerNode.entrySet()) {
            System.out.println(pair.getKey() + "->" + pair.getValue());
        }

        System.out.println("\n\nMem per node");
        for (Entry<Integer, Float> pair : problem.memPerNode.entrySet()) {
            System.out.println(pair.getKey() + "->" + pair.getValue());
        }
        for (int node : problem.usedNodes) {
            problem.onNode.get(node).sort((i, j) -> i - j);
            System.out.println(node + "; -servers:" + problem.onNode.get(node));
            for (int i : problem.onNode.get(node)) {
                System.out.println(
                        i + "-> av:" + problem.cpuAvailable.get(i) + "; maxCost" + problem.serverMaxCost.get(i));
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("CPU needed:" + problem.totalCpuAskedFor);
        System.out.println("MEM needed:" + problem.totalMemAskedFor);
        System.out.println(sol.getCompLocationsArrray());

    }
}
