package io.github.mackzwellz.assertj.custom;

import io.github.mackzwellz.assertj.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface FieldComparisonExcludable {

    /**
     * To exclude a field of an object from comparison, add this to object class.
     * Replace set contents with the fields you want to exclude.
     *
     * <pre>
     *     \@Override
     *     public Set<String> obtainFieldsToIgnoreInEquals() {
     *         return Set.of("fieldName1","fieldName2");
     *     }
     * </pre>
     *
     * @return
     */
    default Set<String> obtainFieldsToIgnoreInEquals() {
        return Collections.emptySet();
    }

    /**
     * To use the same set of fields in both <code>equals()</code> and for comparison output,
     * use this method in each DTO's <code>equals()</code> like this:
     * <pre>
     *     \@Override
     *     boolean equals(ObjectType o) {
     *         if (this == o) {
     *             return true;
     *         }
     *         if (!(o instanceof ObjectType)) {
     *             return false;
     *         }
     *         ObjectType that = (ObjectType) o;
     *         return ComparatorUtil.equalsUsingGetters(this, that, obtainGettersForEquals());
     *     }
     * </pre>
     *
     * @return
     */
    default List<Method> obtainGettersForEquals() {
        List<String> excludedGetters = new ArrayList<>(this.obtainFieldsToIgnoreInEquals());
        return ReflectionUtil.obtainAllGettersExceptFor(this, excludedGetters);
    }

}
