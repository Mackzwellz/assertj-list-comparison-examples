package io.github.mackzwellz.assertj.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BaseDto {

    public Set<String> obtainFieldsToIgnoreInEquals() {
        return Collections.emptySet();
    }

    //recursion-only methods below
    protected static Logger LOG = LoggerFactory.getLogger(BaseDto.class);

    public final boolean equals(Object a, Object b, Method getter) {
        LOG.info("{} verification:", getter.getName());
        boolean result;
        if (a instanceof List) {
            return equals((List) a, (List) b, getter);
        } else {
            a = invokeMethod(a, getter);
            b = invokeMethod(b, getter);
            result = Objects.equals(a, b);
        }
        if (!result) {
            LOG.error("Objects are not equal to each other!");
            LOG.debug("{} verification: Expected --> {}", getter.getName(), a);
            LOG.debug("{} verification: Actual --> {}", getter.getName(), a);
        }
        return result;
    }

    public final boolean equals(Object a, Object b, String field) {
        LOG.info("{} verification: Expected --> {} vs actual --> {}", field, a, b);
        boolean result;
        if (a instanceof List) {
            return equals((List) a, (List) b);
        } else {
            result = Objects.equals(a, b);
        }
        if (!result) {
            LOG.error("Objects are not equal to each other!");
        }
        return result;
    }

    private <T extends Comparable<? super T>> boolean equals(List<T> a, List<T> b, Method getter) {
        LOG.info("{} lists verification:", getter.getName());
        a = (List<T>) invokeMethod(a, getter);
        b = (List<T>) invokeMethod(b, getter);
        boolean result = equals(a, b);
        if (!result) {
            LOG.error("Objects are not equal to each other!");
            LOG.debug("{} lists verification: Expected --> {}", getter.getName(), a);
            LOG.debug("{} lists verification: Actual --> {}", getter.getName(), b);
        }
        return result;
    }

    /**
     * Comparing two Lists of DTO's
     *
     * @param a first list of DTO to be compared
     * @param b second list of DTO to be compared
     *
     * @return result of verification
     */
    private <T extends Comparable<? super T>> boolean equals(List<T> a, List<T> b) {
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

        Collections.sort(a);
        Collections.sort(b);
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
//
//    /**
//     * Method to get a deep copy of DTO
//     * DTO should contain constructor with DTO as an argument
//     * which initializes all properties of the class by mapping from passed DTO
//     * and this method should 'return new ClassName(this);' - new instance with existing properties
//     *
//     * @return Deep copy of object
//     */
//    public abstract T getCopy();

    public List<Method> obtainGettersForEquals() {
        return Collections.emptyList();
    }

    public List<Method> obtainAllGetters() {
        return Arrays.stream(this.getClass().getMethods()) // getDeclaredMethods() ?
                .filter(method -> {
                    String methodName = method.getName();
                    return (methodName.startsWith("get") || method.getName().startsWith("is"))
                           && !
                                   (method.getName().startsWith("getCopy") || method.getName().equals("getClass"));
                })
                .collect(Collectors.toList());
    }

    public List<Method> obtainAllGettersExceptFor(List<String> getterNames) {
        return obtainAllGetters()
                .stream()
                .filter(method -> !getterNames.contains(method.getName()))
                .collect(Collectors.toList());
    }

    //TODO use BaseDto<T> instead of object?
    public boolean equalsUsingGetters(Object expected, Object actual, List<Method> getters) {
        boolean result;
        for (Method getter : getters) {
            result = equals(expected, actual, getter);
            if (!result) {
                return false;
            }
        }
        return true;
    }

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
        Object expectedValue = invokeMethod(expected, getter);
        Object actualValue = invokeMethod(actual, getter);
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

//    private static BaseDto<?> obtainInstance(Type getterReturnType) {
//        try {
//            return ((Class<? extends BaseDto<?>>) getterReturnType).getDeclaredConstructor().newInstance();
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//    }

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

    private static Object invokeMethod(Object object, Method method) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
