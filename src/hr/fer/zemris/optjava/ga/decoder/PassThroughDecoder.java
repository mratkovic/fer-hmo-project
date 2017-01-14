package hr.fer.zemris.optjava.ga.decoder;

import hr.fer.zemris.optjava.ga.solution.IntArraySolution;

public class PassThroughDecoder implements IDecoder<IntArraySolution> {
    @Override
    public int[] decode(final IntArraySolution value) {
        return value.values;
    }

}
