package hr.fer.zemris.optjava.localsearch;

import java.util.List;

import hr.fer.zemris.optjava.ga.solution.SingleObjectiveSolution;

public interface ILocalSearch<T extends SingleObjectiveSolution> {
    public List<T> neighbors(T individual);

}
