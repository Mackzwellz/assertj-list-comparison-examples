package io.github.mackzwellz.assertj.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);

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

    /**
     * @param object
     * @param fieldNamesToExclude FIELD NAMES
     * @param <T>
     *
     * @return METHODS
     */
    public static <T> List<Method> obtainAllGettersExceptFor(T object, List<String> fieldNamesToExclude) {
        return obtainAllGetters(object)
                .stream()
                .filter(method -> fieldNamesToExclude.stream().noneMatch(fieldName -> StringUtils.containsIgnoreCase(method.getName(), fieldName)))
                .collect(Collectors.toList());
    }

    public static Type obtainGetterReturnType(Method getter) {
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

    public static Object invokeMethod(Object object, Method method) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
