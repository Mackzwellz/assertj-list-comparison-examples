package assertj;

import org.assertj.core.api.recursive.comparison.ComparisonDifference;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonDifferenceCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class ComparisonDifferenceTest extends MyBaseTest {

    // https://github.com/assertj/assertj/issues/3152
    // https://github.com/assertj/assertj/issues/2812
    @Test
    public void recursiveComparisonDifferenceTest() {
        // sort-of custom equals
        RecursiveComparisonConfiguration config = new RecursiveComparisonConfiguration();
        config.ignoreCollectionOrder(false);

        RecursiveComparisonDifferenceCalculator calculator = new RecursiveComparisonDifferenceCalculator();
        List<ComparisonDifference> differences = calculator.determineDifferences(actualList, expectedList, config);

        if (differences.isEmpty()) {
            System.out.println("Lists are identical.");
        } else {
            System.out.println("Differences found:");
            for (ComparisonDifference difference : differences) {
                System.out.println(formatDifference(difference));
            }

        }
    }

    private static String formatDifference(ComparisonDifference difference) {
        String message = "Difference in " + String.join(".", difference.getDecomposedPath());
        if (difference.getActual() != null) {
            message += "\nexpected: " + difference.getExpected();
            message += "\nactual: " + difference.getActual();
        }
        return message;
    }

}
