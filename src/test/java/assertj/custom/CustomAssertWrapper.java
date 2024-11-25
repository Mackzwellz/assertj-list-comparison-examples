package assertj.custom;

import io.github.mackzwellz.assertj.custom.FieldComparisonExcludable;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.ComparisonDifference;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonDifferenceCalculator;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.presentation.Representation;
import org.assertj.core.presentation.StandardRepresentation;

import java.util.List;
import java.util.function.Supplier;

/**
 * Custom wrapper for AssertJ's assertions to allow for:
 * - generating human-readable object diffs in assertions
 * - use overridden Object#equals in comparisons both inside and outside of assertion
 * <p>
 * Actual and expected objects must implement {@link FieldComparisonExcludable} or be collections of such objects
 */
public class CustomAssertWrapper {

    private static final RecursiveComparisonConfiguration cfg = new RecursiveComparisonConfiguration();

    static {
        cfg.setIntrospectionStrategy(new CustomIgnoringIntrospectionStrategy());
    }

    private static Supplier<String> customErrorMessageSupplier(Object actual, Object expected, RecursiveComparisonConfiguration cfg) {
        return () -> {
            Representation representation = new StandardRepresentation(); //TODO custom representation?
            RecursiveComparisonDifferenceCalculator diffCalc = new RecursiveComparisonDifferenceCalculator();
            List<ComparisonDifference> differences = diffCalc.determineDifferences(actual, expected, cfg);

            ErrorMessageFactory errorMessageFactory = CustomErrorMessageFactory.customFactory(actual,
                    expected,
                    differences,
                    cfg,
                    representation);
            return errorMessageFactory.create();
        };
    }

    private static void verifyEqualsInternal(Object actual, Object expected) {
        Assertions.assertThat(actual)
                .usingRecursiveComparison(cfg)
                .overridingErrorMessage(customErrorMessageSupplier(actual, expected, cfg))
                .isEqualTo(expected);
    }

    private static void verifyEqualsInternalWithOverride(Object actual, Object expected) {
        Assertions.assertThat(actual)
                .usingRecursiveComparison(cfg)
                .usingOverriddenEquals()
                .overridingErrorMessage(customErrorMessageSupplier(actual, expected, cfg))
                .isEqualTo(expected);
    }

    public static void verifyEquals(Object actual, Object expected) {
        verifyEqualsInternalWithOverride(actual, expected);
    }
}
