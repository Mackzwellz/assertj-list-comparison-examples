package assertj.custom;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.ComparisonDifference;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonDifferenceCalculator;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.presentation.Representation;
import org.assertj.core.presentation.StandardRepresentation;

import java.util.List;
import java.util.function.Supplier;

public class CustomAssertWrapper {


    private Supplier<String> customErrorMessageSupplier(Object actual, Object expected, RecursiveComparisonConfiguration cfg) {
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

    //this is enough
    private void verifyEqualsInternal(Object actual, Object expected) {
        RecursiveComparisonConfiguration cfg = new RecursiveComparisonConfiguration();
        cfg.setIntrospectionStrategy(new CustomIgnoringIntrospectionStrategy());
        Assertions.assertThat(actual)
                .usingRecursiveComparison(cfg)
                .overridingErrorMessage(customErrorMessageSupplier(actual, expected, cfg))
                .isEqualTo(expected);
    }

    //TODO but what if we want to use custom equals instead of introspection strategy
    // AND get pretty difference-per-field output?
    // THEN we need custom diffcalc AND representations most likely
    private void verifyEqualsInternalWithOverride(Object actual, Object expected) {
        RecursiveComparisonConfiguration cfg = new RecursiveComparisonConfiguration();
        cfg.setIntrospectionStrategy(new CustomIgnoringIntrospectionStrategy());
        Assertions.assertThat(actual)
                .usingRecursiveComparison(cfg)
                .usingOverriddenEquals()
                .overridingErrorMessage(customErrorMessageSupplier(actual, expected, cfg))
                .isEqualTo(expected);
    }

    public static void verifyEquals(Object actual, Object expected) {
        new CustomAssertWrapper().verifyEqualsInternalWithOverride(actual, expected);
    }
}
