package io.github.mackzwellz.assertj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ReflectionComparatorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionComparatorUtil.class);

    // reflection-based equals

    public static boolean equalsUsingGetters(Object expected, Object actual, List<Method> getters) {
        boolean result;
        for (Method getter : getters) {
            result = equalsFromGetter(expected, actual, getter);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsFromGetter(Object a, Object b, Method getter) {
        //Type fieldType = ReflectionUtil.obtainGetterReturnType(getter);
        LOG.info("{} verification for {}:", getter.getName(), getter.getDeclaringClass().getSimpleName());
        boolean result;
        if (Collection.class.isAssignableFrom(getter.getReturnType())) { // ~ fieldType instanceOf Collection
            return listEqualsFromGetter(a, b, getter);
            //} else if (FieldComparisonExcludable.class.isAssignableFrom((Class<?>) fieldType)) {
            //TODO recurse lower? no need it seems
        } else {
            a = ReflectionUtil.invokeMethod(a, getter);
            b = ReflectionUtil.invokeMethod(b, getter);
            result = Objects.equals(a, b);
        }
        if (!result) {
            LOG.error("Objects are not equal to each other!");
            LOG.debug("{} verification: Expected --> {}", getter.getName(), a);
            LOG.debug("{} verification: Actual ----> {}", getter.getName(), b);
        }
        return result;
    }

    private static <T extends Comparable<? super T>> boolean listEqualsFromGetter(Object parentA, Object parentB, Method getter) {
        LOG.info("{} lists verification:", getter.getName());
        List<T> a = (List<T>) ReflectionUtil.invokeMethod(parentA, getter);
        List<T> b = (List<T>) ReflectionUtil.invokeMethod(parentB, getter);
        boolean result = ComparatorUtil.listEquals(a, b);
        if (!result) {
            LOG.error("Objects are not equal to each other!");
            LOG.debug("{} lists verification: Expected --> {}", getter.getName(), a);
            LOG.debug("{} lists verification: Actual ----> {}", getter.getName(), b);
        }
        return result;
    }

    // reflection-based comparators

    public static int compareToUsingGetters(Object expected, Object actual, List<Method> getters) {
        int result = 0;
        for (Method getter : getters) {
            result = compareToFromGetter(expected, actual, getter);
            if (result != 0) {
                return result;
            }
        }
        return result;
    }

    // even if parents are Comparable, compare field-by-field anyway?
    // if child is a list, throw it in listCompare. if lists have nested lists, recurse here.
    // if it is comparable, compare it as is
    // if it is not comparable, error out?
    public static <T extends Comparable<? super T>> int compareToFromGetter(Object parentA, Object parentB, Method childGetter) {
        LOG.info("{} comparison for {}:", childGetter.getName(), childGetter.getDeclaringClass().getSimpleName());
        T valueA = (T) ReflectionUtil.invokeMethod(parentA, childGetter);
        T valueB = (T) ReflectionUtil.invokeMethod(parentB, childGetter);

        if (Collection.class.isAssignableFrom(childGetter.getReturnType())) { // ~ fieldType instanceOf Collection
            return ComparatorUtil.listCompareTo((List) valueA, (List) valueB);
        } else {
            return ComparatorUtil.nullSafeCompareTo(valueA, valueB);
        }
    }

}
