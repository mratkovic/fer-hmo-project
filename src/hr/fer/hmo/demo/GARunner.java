package hr.fer.hmo.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import hr.fer.hmo.checker.ConstraintsChecker;
import hr.fer.hmo.checker.FitnessCalculator;
import hr.fer.hmo.checker.Instance;
import hr.fer.hmo.checker.Solution;
import hr.fer.zemris.optjava.dz4.function.IFunction;
import hr.fer.zemris.optjava.dz4.function.InstanceFunction;
import hr.fer.zemris.optjava.dz4.ga.GenerationGA;
import hr.fer.zemris.optjava.ga.cross.ICross;
import hr.fer.zemris.optjava.ga.cross.SinglePointCross;
import hr.fer.zemris.optjava.ga.decoder.IDecoder;
import hr.fer.zemris.optjava.ga.decoder.PassThroughDecoder;
import hr.fer.zemris.optjava.ga.mutation.IMutation;
import hr.fer.zemris.optjava.ga.mutation.ToggleMutation;
import hr.fer.zemris.optjava.ga.selection.ISelection;
import hr.fer.zemris.optjava.ga.selection.RouletteWheelSelection;
import hr.fer.zemris.optjava.ga.selection.Tournament;
import hr.fer.zemris.optjava.ga.solution.IntArraySolution;

public class GARunner {

    private static final double MAX_VAL = 5;
    private static final double MIN_VAL = -5;
    private static final double BLX_ALPHA = 0.8;
    // print every iter
    private static final int PRINT_CNT = 1;

    public static void main(final String[] args) throws IOException {

        /** Default parameters */
        Random rnd = new Random();
        int populationSize = 100;
        double minError = 0.05;
        int maxIter = 100000;

        String instancePath = "./instanca.txt";
        Instance problem = new Instance(instancePath);
        ConstraintsChecker checker = new ConstraintsChecker(problem);
        FitnessCalculator calc = new FitnessCalculator(problem);
        System.out.println("Parsed...\n");
        IFunction f = new InstanceFunction(problem, calc, checker);

        ISelection<IntArraySolution> selection2 = new Tournament<>(3);
        ISelection<IntArraySolution> selection = new RouletteWheelSelection<>();
        ICross<IntArraySolution> cross = new SinglePointCross(rnd);
        IMutation<IntArraySolution> mutation = new ToggleMutation(rnd, 1);
        IDecoder<IntArraySolution> decoder = new PassThroughDecoder();

        List<IntArraySolution> population = generateInitialPopulation(rnd, problem, populationSize);

        GenerationGA<IntArraySolution> ga = new GenerationGA<>(f, decoder, populationSize, minError, maxIter, selection,
                mutation, cross, rnd, 2, true, PRINT_CNT);

        ga.run(population);

    }

    private static List<IntArraySolution> generateInitialPopulation(final Random rnd, final Instance problem,
            final int populationSize) throws IOException {

        int tabooNode = 1;
        ArrayList<Integer> availableServers = new ArrayList<>();
        for (int i = 1; i <= problem.nNodes; ++i) {
            if (i != tabooNode) {
                availableServers.addAll(problem.onNode.get(i));
            }
        }

        int[] possibleValues = new int[availableServers.size()];
        for (int i = 0; i < possibleValues.length; ++i) {
            possibleValues[i] = availableServers.get(i);
        }
        String solPath = "./sol_4292_92777778_1484151396.txt";
        Solution sol = new Solution(solPath);
        List<IntArraySolution> population = new LinkedList<>();
        for (int i = 0; i < populationSize; i++) {
            IntArraySolution s = new IntArraySolution(problem.usedComponents.size(), possibleValues);
            s.randomize(rnd);
            for (int j = 0; j < s.size(); ++j) {
                if (rnd.nextBoolean()) {
                    s.values[j] = sol.getCompLocationsArrray().get(j);
                }
            }

            population.add(s);
        }
        return population;
    }
}
