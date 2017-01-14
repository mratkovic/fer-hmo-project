package hr.fer.zemris.optjava.ga.decoder;

public interface IDecoder<T> {

    public int[] decode(T value);

}