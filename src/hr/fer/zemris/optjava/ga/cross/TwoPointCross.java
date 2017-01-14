package hr.fer.zemris.optjava.ga.cross;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hr.fer.zemris.optjava.ga.solution.IntArraySolution;

public class TwoPointCross implements ICross<IntArraySolution> {
    public Random rnd;

    public TwoPointCross(final Random rnd) {
        super();
        this.rnd = rnd;
    }

    @Override
    public List<IntArraySolution> crossParents(final IntArraySolution p1, final IntArraySolution p2) {
        int n = p1.size();
        IntArraySolution child = p2.duplicate();

        int first = rnd.nextInt(n);
        int second = rnd.nextInt(n);

        for (int i = first; i <= second; i++) {
            child.values[i] = p1.values[i];
        }
        List<IntArraySolution> rv = new ArrayList<>();
        rv.add(child);
        return rv;
    }

}
