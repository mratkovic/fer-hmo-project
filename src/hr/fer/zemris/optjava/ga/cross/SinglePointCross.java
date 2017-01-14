package hr.fer.zemris.optjava.ga.cross;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hr.fer.zemris.optjava.ga.solution.IntArraySolution;

public class SinglePointCross implements ICross<IntArraySolution> {
    public Random rnd;

    public SinglePointCross(final Random rnd) {
        super();
        this.rnd = rnd;
    }

    @Override
    public List<IntArraySolution> crossParents(final IntArraySolution p1, final IntArraySolution p2) {
        int n = p1.size();
        IntArraySolution child = p2.duplicate();

        int k = rnd.nextInt(n);

        for (int i = 0; i < n; i++) {
            if (i < k) {
                child.values[i] = p1.values[i];
            } else {
                child.values[i] = p2.values[i];
            }
        }
        List<IntArraySolution> rv = new ArrayList<>();
        rv.add(child);
        return rv;
    }

}
