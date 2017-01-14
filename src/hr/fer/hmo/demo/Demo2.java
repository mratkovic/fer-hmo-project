package hr.fer.hmo.demo;

import java.io.IOException;
import java.util.Map.Entry;

import hr.fer.hmo.checker.ConstraintsChecker;
import hr.fer.hmo.checker.FitnessCalculator;
import hr.fer.hmo.checker.Instance;
import hr.fer.hmo.checker.Solution;

public class Demo2 {
    public static void main(final String[] args) throws IOException {
        String instancePath = "./instanca.txt";
        Instance problem = new Instance(instancePath);
        System.out.println("Parsed...\n");

        System.out.println("Chains:" + problem.serviceChains.size());
        System.out.println("Links:" + problem.links.size());

        String solPath = "./solutions/3971.000000.txt";
        Solution sol = new Solution(solPath);

        ConstraintsChecker checker = new ConstraintsChecker(problem, true);
        FitnessCalculator calc = new FitnessCalculator(problem);
        double fit = calc.calculate(sol);
        System.out.println(checker.check(sol));
        System.out.println(fit);

        System.out.println(sol.getCompLocationsArrray());
        long startTime = System.currentTimeMillis();

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(estimatedTime);

        for (int node : problem.usedNodes) {
            for (int server : problem.onNode.get(node)) {

                for (Entry<Integer, Integer> pair : sol.componentLocation.entrySet()) {
                    if (pair.getValue() == server) {
                        System.out.print((pair.getKey() - 1) + ", ");
                    }
                }
            }
        }
    }
}
