package hr.fer.zemris.optjava.dz4.ga;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import hr.fer.zemris.optjava.dz4.function.IFunction;
import hr.fer.zemris.optjava.ga.cross.ICross;
import hr.fer.zemris.optjava.ga.decoder.IDecoder;
import hr.fer.zemris.optjava.ga.mutation.IMutation;
import hr.fer.zemris.optjava.ga.selection.ISelection;
import hr.fer.zemris.optjava.ga.solution.SingleObjectiveSolution;
import hr.fer.zemris.optjava.localsearch.ILocalSearch;

public class GenerationGA<T extends SingleObjectiveSolution> {
    IFunction f;
    IFunction fitnessFunction;
    IDecoder<T> decoder;
    int populationSize;
    double minValue;

    int maxIteration;
    ISelection<T> selection;
    IMutation<T> mutation;
    ICross<T> cross;
    ILocalSearch<T> localSearch;
    int keepEliteN;

    Random rnd;

    T best;
    int noChangeCnt;
    int printCnt;

    public GenerationGA(final IFunction f, final IDecoder<T> decoder, final int populationSize, final double minValue,
            final int maxIteration, final ISelection<T> selection, final IMutation<T> mutation, final ICross<T> cross,
            final ILocalSearch<T> localSearch, final Random rnd, final int keepEliteN, final boolean minimize,
            final int printCnt) {
        this.keepEliteN = keepEliteN;

        this.f = f;
        this.decoder = decoder;
        this.populationSize = populationSize;
        this.minValue = minValue;
        this.maxIteration = maxIteration;
        this.selection = selection;
        this.mutation = mutation;
        this.cross = cross;
        this.localSearch = localSearch;
        this.rnd = rnd;
        this.fitnessFunction = new IFunction() {

            @Override
            public double valueAt(final int[] sol) {
                int a = minimize ? -1 : 1;
                return a * f.valueAt(sol);
            }

            @Override
            public int numberOfVariables() {
                return f.numberOfVariables();
            }

            @Override
            public void saveSolution(final int[] sol, final String path) {
                f.saveSolution(sol, path);

            }
        };
        this.printCnt = printCnt;

    }

    GenerationGA(final IFunction f, final IFunction fitness, final IDecoder<T> decoder, final int populationSize,
            final double minValue, final int maxIteration, final ISelection<T> selection, final IMutation<T> mutation,
            final ICross<T> cross, final ILocalSearch<T> localSearch, final Random rnd, final int keepEliteN,
            final boolean minimize, final int printCnt) {

        this.keepEliteN = keepEliteN;
        this.f = f;
        this.decoder = decoder;
        this.populationSize = populationSize;
        this.minValue = minValue;
        this.maxIteration = maxIteration;
        this.selection = selection;
        this.mutation = mutation;
        this.cross = cross;
        this.localSearch = localSearch;
        this.rnd = rnd;
        this.fitnessFunction = fitness;
        this.printCnt = printCnt;

    }

    public void run(List<T> population) {
        evaluatePopulation(population);

        best = population.get(0);
        noChangeCnt = 0;

        int it = 0;
        while (it < maxIteration && best.value > minValue) {
            long startTime = System.currentTimeMillis();
            population = runSingleIteration(population);
            T newBest = population.get(0);
            long estimatedTime = System.currentTimeMillis() - startTime;

            if (it % printCnt == 0) {
                System.out.println(it + "\t\tBest ->  Fitness: " + newBest.fitness + "; Cost: " + newBest.value
                        + "; elapsed: " + estimatedTime);
            }

            best = newBest;
            ++it;

        }
        System.out.println("Iter:\t" + it);
        System.out.println("Best value: " + best.value);
    }

    protected List<T> runSingleIteration(List<T> population) {
        List<T> nextGen = new ArrayList<>();

        for (int i = 0; i < keepEliteN; ++i) {
            nextGen.add(population.get(i));
        }

        while (nextGen.size() < populationSize) {
            T p1 = selection.selectFromPopulation(population, rnd);
            T p2 = selection.selectFromPopulation(population, rnd);
            List<T> children = cross.crossParents(p1, p2);
            T bestChild = mutation.mutate(children.get(0));

            List<T> neighbors = localSearch.neighbors(bestChild);
            // evaluatePopulation(neighbors);
            // for (int i = 0; i < neighbors.size(); ++i) {
            // String name = String.format("solutions/%f.txt",
            // neighbors.get(i).value);
            // if (neighbors.get(i).value < 3980 && !new File(name).isFile()) {
            // f.saveSolution(decoder.decode(neighbors.get(i)), name);
            // System.out.println("SAVED" + name);
            // }
            // }

            nextGen.add(neighbors.get(0));

        }
        population.clear();
        population = nextGen;

        evaluatePopulation(population);
        for (int i = 0; i < population.size(); ++i) {
            String name = String.format("solutions/%f.txt", population.get(i).value);
            if (population.get(i).value < 3980 && !new File(name).isFile()) {
                f.saveSolution(decoder.decode(population.get(i)), name);
                System.out.println("SAVED" + name);
            }
        }
        return population;
    }

    private final Comparator<T> comparator = (p1, p2) -> Double.compare(p1.fitness, p2.fitness);
    private final Comparator<T> descComparator = (p1, p2) -> -Double.compare(p1.fitness, p2.fitness);

    protected T findBest(final List<T> population) {
        evaluatePopulation(population);
        return population.get(0);
    }

    protected void evaluatePopulation(final List<T> population) {
        long startTime = System.currentTimeMillis();
        for (T sol : population) {
            evaluate(sol);
        }

        long estimatedTime = System.currentTimeMillis() - startTime;
        // System.out.println(population.size() + "; elapsed: " +
        // estimatedTime);
        population.sort(descComparator);
    }

    protected void evaluate(final T sol) {
        int[] fpSol = decoder.decode(sol);
        sol.value = f.valueAt(fpSol);
        sol.fitness = fitnessFunction.valueAt(fpSol);

    }
}
