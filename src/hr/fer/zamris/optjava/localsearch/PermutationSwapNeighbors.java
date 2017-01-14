package hr.fer.zamris.optjava.localsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hr.fer.zemris.optjava.ga.solution.Permutation;

public class PermutationSwapNeighbors implements ILocalSearch<Permutation> {

    Random rnd;
    int kSwaps;

    int n;

    public PermutationSwapNeighbors(final Random rnd, final int kSwaps, final int n) {
        super();
        this.rnd = rnd;
        this.kSwaps = kSwaps;
        this.n = n;
    }

    @Override
    public List<Permutation> neighbors(final Permutation individual) {
        List<Permutation> ns = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            for (int k = 0; k < kSwaps; ++k) {
                Permutation d = individual.duplicate();
                int r1 = rnd.nextInt(individual.size());
                int r2 = rnd.nextInt(individual.size());

                d.values[r1] = individual.values[r2];
                d.values[r2] = individual.values[r1];
                ns.add(d);
            }
        }
        return ns;
    }

}
