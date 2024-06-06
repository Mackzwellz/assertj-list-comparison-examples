package io.github.mackzwellz.assertj.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionUtil {

    public static <T> List<Method> obtainAllGetters(T object) {
        return Arrays.stream(object.getClass().getMethods()) // getDeclaredMethods() ?
                .filter(method -> {
                    String methodName = method.getName();
                    return (methodName.startsWith("get") || method.getName().startsWith("is"))
                           && !
                                   (method.getName().startsWith("getCopy") || method.getName().equals("getClass"));
                })
                .collect(Collectors.toList());
    }

    public static <T> List<Method> obtainAllGettersExceptFor(T object, List<String> getterNamesToExclude) {
        return obtainAllGetters(object)
                .stream()
                .filter(method -> !getterNamesToExclude.contains(method.getName()))
                .collect(Collectors.toList());
    }

    public static Object invokeMethod(Object object, Method method) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
