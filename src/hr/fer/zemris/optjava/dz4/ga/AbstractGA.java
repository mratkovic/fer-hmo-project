package hr.fer.zemris.optjava.dz4.ga;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import hr.fer.zemris.optjava.dz4.function.IFunction;
import hr.fer.zemris.optjava.ga.cross.ICross;
import hr.fer.zemris.optjava.ga.decoder.IDecoder;
import hr.fer.zemris.optjava.ga.mutation.IMutation;
import hr.fer.zemris.optjava.ga.selection.ISelection;
import hr.fer.zemris.optjava.ga.solution.SingleObjectiveSolution;

public abstract class AbstractGA<T extends SingleObjectiveSolution> {
    private static final double EPS = 1e-5;
    private static final int UPDATE_MUTATION_CNT = 3000;

    IFunction f;
    IFunction fitnessFunction;
    IDecoder<T> decoder;
    int populationSize;
    double minValue;

    int maxIteration;
    ISelection<T> selection;
    IMutation<T> mutation;
    ICross<T> cross;

    Random rnd;

    T best;
    int noChangeCnt;
    int printCnt;

    public AbstractGA(final IFunction f, final IDecoder<T> decoder, final int populationSize, final double minValue,
            final int maxIteration, final ISelection<T> selection, final IMutation<T> mutation, final ICross<T> cross,
            final Random rnd, final boolean minimize, final int printCnt) {
        super();
        this.f = f;
        this.decoder = decoder;
        this.populationSize = populationSize;
        this.minValue = minValue;
        this.maxIteration = maxIteration;
        this.selection = selection;
        this.mutation = mutation;
        this.cross = cross;
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

    public AbstractGA(final IFunction f, final IFunction fitness, final IDecoder<T> decoder, final int populationSize,
            final double minValue, final int maxIteration, final ISelection<T> selection, final IMutation<T> mutation,
            final ICross<T> cross, final Random rnd, final boolean minimize, final int printCnt) {
        super();
        this.f = f;
        this.decoder = decoder;
        this.populationSize = populationSize;
        this.minValue = minValue;
        this.maxIteration = maxIteration;
        this.selection = selection;
        this.mutation = mutation;
        this.cross = cross;
        this.rnd = rnd;
        this.fitnessFunction = fitness;
        this.printCnt = printCnt;

    }

    public void run(List<T> population) {
        long startTime = System.nanoTime();
        evaluatePopulation(population);

        best = population.get(0);
        noChangeCnt = 0;

        int it = 0;
        while (it < maxIteration && best.value > minValue) {
            population = runSingleIteration(population);
            T newBest = population.get(0);
            HashSet<T> pop = new HashSet<>(population);

            // if no change last N iterations, reduce mutation
            testForMutationUpdate(best, newBest);

            if (it % printCnt == 0) {
                System.out.println(
                        it + "\t\tBest ->  Fitness: " + best.fitness + "; Cost: " + best.value + ";" + pop.size());
            }
            if (it % 100 == 0) {
                String dumpPath = String.format("solutions/best_%d_%f.txt", it, best.value);
                f.saveSolution(decoder.decode(best), dumpPath);
                System.out.println("Solution saved to" + dumpPath);
            }
            best = newBest;

            ++it;

        }
        System.out.println("Iter:\t" + it);
        System.out.println("Best value: " + best.value);
        System.out.println("Time:" + (System.nanoTime() - startTime) / 1e9 + "s");
    }

    protected abstract List<T> runSingleIteration(final List<T> population);

    private void testForMutationUpdate(final T best, final T newBest) {
        if (Math.abs(newBest.fitness - best.fitness) < EPS) {
            noChangeCnt++;
            if (noChangeCnt == UPDATE_MUTATION_CNT) {
                noChangeCnt = 0;
            }
        }
    }

    private final Comparator<T> comparator = (p1, p2) -> Double.compare(p1.fitness, p2.fitness);
    private final Comparator<T> descComparator = (p1, p2) -> -Double.compare(p1.fitness, p2.fitness);

    protected T findBest(final List<T> population) {
        evaluatePopulation(population);
        return population.get(0);
    }

    protected void evaluatePopulation(final List<T> population) {
        for (T sol : population) {
            int[] fpSol = decoder.decode(sol);
            sol.value = f.valueAt(fpSol);
            sol.fitness = fitnessFunction.valueAt(fpSol);
        }
        population.sort(descComparator);
    }
}
