package assertj.custom;

import org.assertj.core.api.recursive.comparison.ComparisonDifference;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.error.ShouldBeEqualByComparingFieldByFieldRecursively;
import org.assertj.core.presentation.Representation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.util.Strings.escapePercent;
import static org.assertj.core.util.Strings.join;

/**
 * @see ShouldBeEqualByComparingFieldByFieldRecursively
 */
public class CustomErrorMessageFactory extends BasicErrorMessageFactory {

    private CustomErrorMessageFactory(String message, Object... arguments) {
        super(message, arguments);
    }

    public static <T> ErrorMessageFactory customFactory(T actual, T expected,
                                                        List<ComparisonDifference> differences,
                                                        RecursiveComparisonConfiguration recursiveComparisonConfiguration,
                                                        Representation representation) {
        String differencesDescription = join(differences.stream()
                .map(difference -> difference.multiLineDescription(representation))
                .collect(toList())).with(format("%n%n"));
        //omitting description for now. can uncomment later if required.
        //String recursiveComparisonConfigurationDescription = recursiveComparisonConfiguration.multiLineDescription(representation);
        String differencesCount = differences.size() == 1 ? "difference:%n" : "%s differences:%n";

        String comparedObjectTypes = getObjectTypesString(actual, expected);

        // @format:off
        //I wish AssertJ allowed me to pass custom error message factories into assertions, so I wouldn't have to do all of this
        //A man can dream (or make an issue or a PR)
        return new CustomErrorMessageFactory("%n" +
                "Equals mismatch for " + comparedObjectTypes + ":%n" +
                "Found " + differencesCount +
                "%n" + escapePercent(differencesDescription)
//                "%n%n" +
//                "The recursive comparison was performed with this configuration:%n" +
//                recursiveComparisonConfigurationDescription,
                // don't use %s to avoid AssertJ formatting String with ""
                , differences.size());
        // @format:on
    }

    private static <T> String getObjectTypesString(T actual, T expected) {
        //TODO any way to know if object are of different types all the way down without stringifying both?
        String actualTypeChain = getAllParametersRecursively(actual);
        String expectedTypeChain = getAllParametersRecursively(expected);
        return expectedTypeChain.equals(actualTypeChain) ?
                expectedTypeChain :
                String.format(":%n actual %s%n and expected %s", expectedTypeChain, actualTypeChain);
    }

    private static <T> String getAllParametersRecursively(T objectOfMaybeParametrizedClass) {
        //TODO use recursion? still OK for now to get basic chain
        List<List<? extends Type>> listOfAllTypes = new ArrayList<>();
        listOfAllTypes.add(Collections.singletonList(objectOfMaybeParametrizedClass.getClass()));
        if (Collection.class.isAssignableFrom(objectOfMaybeParametrizedClass.getClass())) {
            Type lv1Type = ((Collection<?>) objectOfMaybeParametrizedClass).toArray()[0].getClass();
            listOfAllTypes.add(Collections.singletonList(lv1Type));
            if (lv1Type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) lv1Type;
                List<Type> argTypes = Arrays.asList(paramType.getActualTypeArguments());
                listOfAllTypes.add(argTypes);
            }
        }
        return formatChain(listOfAllTypes);
    }

    /**
     * @param typesChain [ArrayList],[User],[HashMap],[Key,Value]
     * @return string like "<Key, Value> in <HashMap> in <User> in <ArrayList>":
     */
    public static String formatChain(List<List<? extends Type>> typesChain) {
        List<String> classStrings = new ArrayList<>();
        for (int i = typesChain.size() - 1; i >= 0; i--) {
            String enclosedType = typesChain.get(i).
                    stream()
                    .map(t -> ((Class<?>) t).getSimpleName())
                    .collect(joining(", ", "<", ">"));
            classStrings.add(enclosedType);
        }
        return String.join(" in ", classStrings);
    }
}




