package hr.fer.zemris.optjava.ga.decoder;

import hr.fer.zemris.optjava.ga.solution.Permutation;

public class PermutationPassThroughDecoder implements IDecoder<Permutation> {
    @Override
    public int[] decode(final Permutation value) {
        return value.values;
    }

}
