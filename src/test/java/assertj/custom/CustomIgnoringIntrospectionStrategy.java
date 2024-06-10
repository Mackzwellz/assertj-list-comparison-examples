package assertj.custom;

import io.github.mackzwellz.assertj.custom.FieldComparisonExcludable;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonIntrospectionStrategy;
import org.assertj.core.internal.Objects;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.assertj.core.util.introspection.PropertyOrFieldSupport.COMPARISON;

/**
 * Custom comparison strategy that allows us to exclude fields that we don't want to compare.
 * Excluded fields are defined for each object by {@link FieldComparisonExcludable#obtainFieldsToIgnoreInEquals()}
 */
public class CustomIgnoringIntrospectionStrategy implements RecursiveComparisonIntrospectionStrategy {

    // use ConcurrentHashMap in case this strategy instance is used in a multi-thread context
    private final Map<Class<?>, Set<String>> fieldNamesPerClass = new ConcurrentHashMap<>();

    @Override
    public Set<String> getChildrenNodeNamesOf(Object node) {
        if (node == null) return new HashSet<>();
        // Caches the names after getting them for efficiency, a node can be introspected multiple times for example if
        // it belongs to an unordered collection as all actual elements are compared to all expected elements.
        if (node instanceof FieldComparisonExcludable) {
            return getFieldsNamesCustomSimple(node);
            //return fieldNamesPerClass.computeIfAbsent(node.getClass(), this::getFieldsNamesCustom);
        }
        return fieldNamesPerClass.computeIfAbsent(node.getClass(), Objects::getFieldsNames);
    }

    public Set<String> getFieldsNamesCustom(Class<?> clazz) {
        Set<String> excluded = new HashSet<>();
        if (clazz.isAssignableFrom(FieldComparisonExcludable.class)) { //TODO fixme
            try {
                excluded = ((FieldComparisonExcludable) clazz.newInstance()).obtainFieldsToIgnoreInEquals();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Set<String> finalExcluded = new HashSet<>(excluded);
        finalExcluded.add("LOG");
        return Objects.getFieldsNames(clazz).stream().filter(f -> !finalExcluded.contains(f)).collect(Collectors.toSet());
    }

    public Set<String> getFieldsNamesCustomSimple(Object node) {
        Set<String> excluded = new HashSet<>();
        if (node instanceof FieldComparisonExcludable) {
            excluded = ((FieldComparisonExcludable) node).obtainFieldsToIgnoreInEquals();
        }
        Set<String> finalExcluded = excluded;
        return Objects.getFieldsNames(node.getClass()).stream().filter(f -> !finalExcluded.contains(f)).collect(Collectors.toSet());
    }

    @Override
    public Object getChildNodeValue(String childNodeName, Object instance) {
        return COMPARISON.getSimpleValue(childNodeName, instance);
    }
}

