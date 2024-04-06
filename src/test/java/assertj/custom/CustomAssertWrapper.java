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
            // deals with both actual and expected being null
//        if (actual == expected) return myself;
//        if (expected == null) {
//            // for the assertion to pass, actual must be null but this is not the case since actual != expected
//            // => we fail expecting actual to be null
//            Objects.assertNull(info, actual);
//        }
//        // at this point expected is not null, which means actual must not be null for the assertion to pass
//        objects.assertNotNull(info, actual);
            // at this point both actual and expected are not null, we can compare them recursively!

            //above not needed since this is a glorified tostring?? todo verify this claim

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
        //return myself;
    }

    private void verifyEqualsInternal(Object actual, Object expected) {
        RecursiveComparisonConfiguration cfg = new RecursiveComparisonConfiguration();
        cfg.setIntrospectionStrategy(new CustomIgnoringIntrospectionStrategy());
        Assertions.assertThat(actual)
                .usingRecursiveComparison(cfg)
                .overridingErrorMessage(customErrorMessageSupplier(actual, expected, cfg))
                .isEqualTo(expected);
    }

    public static void verifyEquals(Object actual, Object expected) {
        new CustomAssertWrapper().verifyEqualsInternal(actual, expected);
    }
}
