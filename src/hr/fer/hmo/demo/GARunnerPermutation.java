package hr.fer.hmo.demo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import hr.fer.hmo.checker.ConstraintsChecker;
import hr.fer.hmo.checker.FitnessCalculator;
import hr.fer.hmo.checker.Instance;
import hr.fer.zemris.optjava.dz4.function.IFunction;
import hr.fer.zemris.optjava.dz4.function.InstanceFunctionPermutation;
import hr.fer.zemris.optjava.dz4.ga.GenerationGA;
import hr.fer.zemris.optjava.ga.cross.ICross;
import hr.fer.zemris.optjava.ga.cross.OX2Cross;
import hr.fer.zemris.optjava.ga.decoder.IDecoder;
import hr.fer.zemris.optjava.ga.decoder.PermutationPassThroughDecoder;
import hr.fer.zemris.optjava.ga.mutation.IMutation;
import hr.fer.zemris.optjava.ga.mutation.SwapMutation;
import hr.fer.zemris.optjava.ga.selection.ISelection;
import hr.fer.zemris.optjava.ga.selection.RouletteWheelSelection;
import hr.fer.zemris.optjava.ga.selection.Tournament;
import hr.fer.zemris.optjava.ga.solution.Permutation;
import hr.fer.zemris.optjava.localsearch.ILocalSearch;
import hr.fer.zemris.optjava.localsearch.PermutationSwapNeighbors;

public class GARunnerPermutation {

    private static final double MAX_VAL = 5;
    private static final double MIN_VAL = -5;
    private static final double BLX_ALPHA = 0.8;
    // print every iter
    private static final int PRINT_CNT = 1;

    public static void main(final String[] args) throws IOException {

        /** Default parameters */
        Random rnd = new Random();
        int populationSize = 25;
        double minError = 0.05;
        int maxIter = 100000;

        String instancePath = "./instanca.txt";
        Instance problem = new Instance(instancePath);
        ConstraintsChecker checker = new ConstraintsChecker(problem);
        FitnessCalculator calc = new FitnessCalculator(problem);
        System.out.println("Parsed...\n");
        IFunction f = new InstanceFunctionPermutation(problem, calc, checker);

        ISelection<Permutation> selection2 = new Tournament<>(1);
        ISelection<Permutation> selection = new RouletteWheelSelection<>();
        ICross<Permutation> cross = new OX2Cross(rnd);
        IMutation<Permutation> mutation = new SwapMutation(rnd);
        IDecoder<Permutation> decoder = new PermutationPassThroughDecoder();
        ILocalSearch<Permutation> localSearch = new PermutationSwapNeighbors(rnd, 2, 30);

        List<Permutation> population = generateInitialPopulation(rnd, problem, populationSize);

        GenerationGA<Permutation> ga = new GenerationGA<>(f, decoder, populationSize, minError, maxIter, selection,
                mutation, cross, localSearch, rnd, 1, true, PRINT_CNT);

        ga.run(population);

    }

    private static List<Permutation> generateInitialPopulation(final Random rnd, final Instance problem,
            final int populationSize) throws IOException {

        List<Permutation> population = new LinkedList<>();
        int[] initial = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 32, 35, 18, 33, 34, 31, 41, 40,
                39, 38, 37, 36, 21, 20, 19, 24, 23, 22, 27, 26, 25, 30, 29, 28 };

        for (int i = 0; i < populationSize; i++) {
            Permutation s = new Permutation(problem.usedComponents.size());

            s.randomize(rnd);
            for (int k = 0; k < s.size(); ++k) {
                s.values[k] = initial[k];
            }
            population.add(s);
        }
        return population;
    }
}
