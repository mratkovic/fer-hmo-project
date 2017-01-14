package hr.fer.hmo.demo;

import java.io.IOException;

import hr.fer.hmo.checker.ConstraintsChecker;
import hr.fer.hmo.checker.FitnessCalculator;
import hr.fer.hmo.checker.Instance;
import hr.fer.hmo.checker.Solution;

public class Demo {
    public static void main(final String[] args) throws IOException {
        String instancePath = "./instanca.txt";
        Instance problem = new Instance(instancePath);
        System.out.println("Parsed...\n");

        System.out.println("Chains:" + problem.serviceChains.size());
        System.out.println("Links:" + problem.links.size());

        String solPath = "./sol_4292_92777778_1484151396.txt";
        Solution sol = new Solution(solPath);

        ConstraintsChecker checker = new ConstraintsChecker(problem);
        FitnessCalculator calc = new FitnessCalculator(problem);
        double fit = calc.calculate(sol);
        System.out.println(checker.check(sol));
        System.out.println(fit);

        System.out.println("\n\nUsed nodes" + problem.usedNodes);

        System.out.println("Number of components: " + problem.nVms);
        System.out.println("Used components" + problem.usedComponents);

    }
}
