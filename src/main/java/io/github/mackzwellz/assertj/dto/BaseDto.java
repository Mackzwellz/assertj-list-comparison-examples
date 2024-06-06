package io.github.mackzwellz.assertj.dto;

import io.github.mackzwellz.assertj.custom.FieldComparisonExcludable;
import io.github.mackzwellz.assertj.util.ReflectionUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BaseDto implements FieldComparisonExcludable {

    //recursion-only methods below
    protected static Logger LOG = LoggerFactory.getLogger(BaseDto.class);

    //public static <T extends BaseDto<? super T>> String customEqualsToString(T expected, T actual, String customMessage) {
    public static <T extends BaseDto> String customEqualsToString(T expected, T actual, String customMessage) {
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
            Type getterReturnType = obtainGetterReturnType(getter);
            if (expectedValue instanceof Collection) {
                putCollectionDiffToMap(map, (Collection) expectedValue, (Collection) actualValue, getter);
            } else if (BaseDto.class.isAssignableFrom((Class<?>) getterReturnType)) {
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
            List<Method> methodsToCheck = ((BaseDto) expectedList.get(0)).obtainGettersForEquals();
            for (int i = 0; i < expectedList.size(); i++) {
                for (Method method : methodsToCheck) {
                    putObjectDiffToMap(map, expectedList.get(i), actualList.get(i), method);
                }
            }
        }
    }

    private static Type obtainGetterReturnType(Method getter) {
        Class<?> returnClass = getter.getReturnType();
        Type[] argTypes;
        if (Collection.class.isAssignableFrom(returnClass)) {
            Type returnType = getter.getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) returnType;
                argTypes = paramType.getActualTypeArguments();
                if (argTypes.length > 0) {
                    LOG.debug("Found parametrized return type {} in getter {}", argTypes[0], getter.getName());
                    return argTypes[0];
                }
            }
        }
        //Dummy value
        return String.class;
    }

}
