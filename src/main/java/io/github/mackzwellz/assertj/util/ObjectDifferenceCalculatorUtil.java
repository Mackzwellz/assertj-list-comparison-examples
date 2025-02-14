package io.github.mackzwellz.assertj.util;

import io.github.mackzwellz.assertj.custom.FieldComparisonExcludable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class ObjectDifferenceCalculatorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectDifferenceCalculatorUtil.class);

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
