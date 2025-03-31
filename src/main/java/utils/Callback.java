package utils;


/**
 * @author Sin
 */
@FunctionalInterface
public interface Callback<T> {
    void onSubmit(T data);
}

