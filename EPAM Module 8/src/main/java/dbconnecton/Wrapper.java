package dbconnecton;

@FunctionalInterface
public interface Wrapper<T> {
    T toSrc();
}
