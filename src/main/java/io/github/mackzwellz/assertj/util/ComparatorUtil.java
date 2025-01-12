package io.github.mackzwellz.assertj.util;

import io.github.mackzwellz.assertj.custom.FieldComparisonExcludable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class ComparatorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ComparatorUtil.class);

    // regular equals

    public static boolean equals(Object a, Object b, String field) {
        LOG.info("{} verification: Expected --> {} vs actual --> {}", field, a, b);
        boolean result;
        if (a instanceof List) {
            return listEquals((List) a, (List) b);
        } else {
            result = Objects.equals(a, b);
        }
        if (!result) {
            LOG.error("Objects are not equal to each other!");
        }
        return result;
    }

    /**
     * Comparing two Lists of DTO's
     *
     * @param a first list of DTO to be compared
     * @param b second list of DTO to be compared
     * @return result of verification
     */
    public static <T> boolean listEquals(List<T> a, List<T> b) {
        LOG.info("Comparing two Lists of DTO's");
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            LOG.debug("List sizes are not equal");
            return false;
        }

        //TODO should likely get rid of this and sort fields after filling the DTO with data.
        //Collections.sort(a);
        //Collections.sort(b);
        LOG.info("First List {}", a);
        LOG.info("Second List {}", b);

        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                LOG.info("{} element from first list:\n '{}' \nis not equal to element from the second list:\n '{}'",
                        i, a.get(i), b.get(i));
                return false;
            }
        }
        return true;
    }

    /**
     * TODO return the diff and compare at higher level?
     *
     * @param expected
     * @param actual
     * @return true if maps are equal, throws SoftAssertionError otherwise
     */
    public static <K, V> boolean verifyMaps(Map<K, V> expected, Map<K, V> actual) {
        Assertions.assertThat(actual.keySet())
                .describedAs("Mismatch of map keys")
                .isEqualTo(expected.keySet());
        try (AutoCloseableSoftAssertions softAssertion = new AutoCloseableSoftAssertions()) {
            for (K key : actual.keySet()) {
                LOG.debug("Comparing key: " + key);
                V expectedValue = expected.get(key);
                V actualValue = actual.get(key);
                LOG.debug("expected value: {}", expectedValue);
                LOG.debug("actual value: {}", actualValue);
                softAssertion.assertThat(actualValue)
                        .describedAs("Value mismatch for key %s", key)
                        .isEqualTo(expectedValue);
            }
        }
        return true;
    }

    public static boolean compareMaps(HashMap<String, List> expected, HashMap<String, List> actual, List<String> ignoreList) {

        if (expected.size() != actual.size()) {
            return false;
        }

        if (!expected.keySet().equals(actual.keySet())) {
            LOG.info("Actual values List not present is expected {}", expected.remove(actual));
            LOG.info("Expected values List not present is actual {}", actual.remove(expected));
            return false;
        }

        for (String col : actual.keySet()) {

            LOG.info("Comparing col : " + col);
            if (!ignoreList.contains(col)) {

                if (expected.get(col).size() != actual.get(col).size()) {
                    LOG.info("Comparing col size {} vs {}: ", expected.get(col).size(), actual.get(col).size());
                    return false;
                }

                LOG.info("Expected values {}", expected.get(col));
                LOG.info("Actual values {}", actual.get(col));

                if (!(new HashSet<>(expected.get(col)).equals(new HashSet<>(actual.get(col))))) {
                    return false;
                }

            }
        }

        return true;
    }

    public static <T> boolean verifyObjects(List<T> expected, List<T> actual) {

        if (expected == null && actual == null) {
            // continue
        } else {

            for (int i = 0; i < expected.size(); i++) {

                if (!expected.get(i).equals(actual.get(i))) {
                    return false;

                }
            }
        }
        return true;

    }

    // reflection-based equals

    public static boolean equalsUsingGetters(Object expected, Object actual, List<Method> getters) {
        boolean result;
        for (Method getter : getters) {
            result = equals(expected, actual, getter);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Object a, Object b, Method getter) {
        //Type fieldType = ReflectionUtil.obtainGetterReturnType(getter);
        LOG.info("{} verification for {}:", getter.getName(), getter.getDeclaringClass().getSimpleName());
        boolean result;
        if (Collection.class.isAssignableFrom(getter.getReturnType())) { // ~ fieldType instanceOf Collection
            return listEquals(a, b, getter);
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

    private static <T extends Comparable<? super T>> boolean listEquals(Object parentA, Object parentB, Method getter) {
        LOG.info("{} lists verification:", getter.getName());
        List<T> a = (List<T>) ReflectionUtil.invokeMethod(parentA, getter);
        List<T> b = (List<T>) ReflectionUtil.invokeMethod(parentB, getter);
        boolean result = listEquals(a, b);
        if (!result) {
            LOG.error("Objects are not equal to each other!");
            LOG.debug("{} lists verification: Expected --> {}", getter.getName(), a);
            LOG.debug("{} lists verification: Actual ----> {}", getter.getName(), b);
        }
        return result;
    }

    public static <T extends FieldComparisonExcludable> String customEqualsToString(T expected, T actual, String customMessage) {
        String a = String.format("Mismatching %s DTOs:\n", expected.getClass().getSimpleName());
        StringBuilder s = new StringBuilder(a);
        Map<Method, Pair<Object, Object>> mismatchesMap = new HashMap<>();
        List<Method> methodsToCheck = expected.obtainGettersForEquals();
        for (Method method : methodsToCheck) {
            putObjectDiffToMap(mismatchesMap, expected, actual, method);
        }

        mismatchesMap.forEach((getter, value) -> {
            Object expectedValue = value.getLeft();
            Object actualValue = value.getRight();
            s.append(String.format("%s->%s:\n", getter.getDeclaringClass().getSimpleName(), getter.getName()));
            s.append("Expected: ").append(expectedValue).append("\n");
            s.append("Actual: ").append(actualValue).append("\n\n");
        });
        return s.toString();

    }

    private static void putObjectDiffToMap(Map<Method, Pair<Object, Object>> map, Object expected, Object actual, Method getter) {
        Object expectedValue = ReflectionUtil.invokeMethod(expected, getter);
        Object actualValue = ReflectionUtil.invokeMethod(actual, getter);
        LOG.debug("Equals for {}", getter.getDeclaringClass().getSimpleName());
        if (!Objects.equals(actualValue, expectedValue)) {
            Type getterReturnType = ReflectionUtil.obtainGetterReturnType(getter);
            if (expectedValue instanceof Collection) {
                putCollectionDiffToMap(map, (Collection) expectedValue, (Collection) actualValue, getter);
            } else if (FieldComparisonExcludable.class.isAssignableFrom((Class<?>) getterReturnType)) {
                putObjectDiffToMap(map, expectedValue, actualValue, getter);
            } else {
                //just put the field in
                map.put(getter, new ImmutablePair<>(expectedValue, actualValue));
            }
        }
    }

    private static <T extends Comparable<?>> void putCollectionDiffToMap
            (Map<Method, Pair<Object, Object>> map, Collection<T> expected, Collection<T> actual, Method getter) {
        LOG.info("Comparing two Collections of DTO's");
        if (expected.size() != actual.size()) {
            LOG.debug("Collection sizes are not equal");
            String message = "collection size is ";
            putObjectDiffToMap(map, message + expected.size(), message + actual.size(), getter);
        } else {
            //        Collections.sort(expected);
            //        Collections.sort(actual);
            //LOG.info("First List {}", expected);
            //LOG.info("Second List {}", actual);
            List<Object> expectedList = new ArrayList<>(expected);
            List<Object> actualList = new ArrayList<>(actual);

            LOG.info("Recursion for {}", getter.getDeclaringClass().getSimpleName());
            //List<Method> methodsToCheck = getInstance(getterReturnType).obtainGettersForEquals();
            List<Method> methodsToCheck = ((FieldComparisonExcludable) expectedList.get(0)).obtainGettersForEquals();
            for (int i = 0; i < expectedList.size(); i++) {
                for (Method method : methodsToCheck) {
                    putObjectDiffToMap(map, expectedList.get(i), actualList.get(i), method);
                }
            }
        }
    }

}