package hr.fer.zemris.optjava.dz4.function;

public interface IFunction {

    public double valueAt(int[] sol);

    public int numberOfVariables();

    public void saveSolution(int[] sol, String path);

}
