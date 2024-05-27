package fintech;

@FunctionalInterface
public interface Resolver<T> {
    T get();
}
