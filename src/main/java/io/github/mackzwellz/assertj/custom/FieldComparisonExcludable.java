package io.github.mackzwellz.assertj.custom;

import java.util.Collections;
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
}
