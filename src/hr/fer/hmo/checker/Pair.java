package hr.fer.hmo.checker;

/**
 * Razred koji predstavlja strukturu para koja se sastoji od dva podatka koji
 * nemoraju biti nuzno istih tipova.
 *
 * @author Marko Ratkovic
 * @version 1.0
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1, T2> {
    public T1 first;
    public T2 second;

    public Pair(final T1 first, final T2 second) {
        super();
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!first.equals(other.first)) {
            return false;
        }
        if (second == null) {
            if (other.second != null) {
                return false;
            }
        } else if (!second.equals(other.second)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

}
