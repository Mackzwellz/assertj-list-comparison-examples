package io.github.mackzwellz.assertj.reflect;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.List;

@Getter
@Setter
public class MismatchDataHolder {

    private Object expectedValue;
    private Object actualValue;
    /**
     * Method used to get expected and actual values
     * e.g. 'Address->getStreet' in 'User->Address->getStreet'
     */
    private Method getter;
    /**
     * Classes of higher level than the one where the getter is from.
     * e.g. 'User' in 'User->Address->getStreet'
     */
    private List<Class<?>> parentClassesHierarchy;

    /**
     * Position of the item in the list of items, in case might be multiple of them.
     * Should be stored based on declaring/actual class of the getter.
     * If null, should not be displayed by default
     * e.g. '[1]' and '[0]' in 'User[1]->Address[0]->getStreet'
     */
    private Integer listItemNumber = null;

    @Override
    public String toString() {
        //String a = String.format("Mismatching %s DTOs:\n", expected.getClass().getSimpleName());
        //StringBuilder s = new StringBuilder(a);
        StringBuilder s = new StringBuilder();
        for (Class<?> clz : parentClassesHierarchy) {
            s.append(String.format("%s->", clz));
        }
        s.append(String.format("%s%s->%s:\n", getter.getDeclaringClass().getSimpleName(), listItemNumber, getter.getName()));
        s.append("Expected: ").append(this.expectedValue).append("\n");
        s.append("Actual: ").append(this.actualValue).append("\n\n");
        return s.toString();
    }

}

