package hr.fer.zemris.optjava.ga.solution;

import java.util.Arrays;
import java.util.Random;

public class IntArraySolution extends SingleObjectiveSolution {

    public int[] values;
    public int[] possibleValues;

    public IntArraySolution(final int size, final int[] possibleValues) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size argument");
        }

        this.values = new int[size];
        this.possibleValues = possibleValues;
    }

    public IntArraySolution newLikeThis() {
        return new IntArraySolution(values.length, possibleValues);
    }

    @Override
    public IntArraySolution duplicate() {
        IntArraySolution d = new IntArraySolution(values.length, possibleValues);
        d.values = Arrays.copyOf(values, values.length);
        return d;
    }

    public void randomize(final Random random) {

        for (int i = 0; i < values.length; i++) {
            int index = random.nextInt(possibleValues.length);
            values[i] = possibleValues[index];
        }
    }

    /** Array size. */
    public int size() {
        return values.length;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IntArraySolution other = (IntArraySolution) obj;
        if (!Arrays.equals(values, other.values)) {
            return false;
        }
        return true;
    }

}
