package fintech;

import com.google.common.base.Throwables;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Supplier;

public class PojoUtils {

    public static void copyProperties(Object to, Object from) {
        try {
            BeanUtils.copyProperties(to, from);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> T cloneBean(T source) {
        try {
            return (T) BeanUtils.cloneBean(source);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> Optional<T> npeSafe(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> safe(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
