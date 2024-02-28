package assertj;

import assertj.custom.CustomIgnoringIntrospectionStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomAssertionsTest extends MyBaseTest {

    @Test
    public void recursiveComparisonWithOverriddenEqualsTest() {
        // sort-of custom equals
        Assertions.assertThat(actualList)
                .usingRecursiveComparison()
                .withIntrospectionStrategy(new CustomIgnoringIntrospectionStrategy()) //ignores names of User
                //.usingOverriddenEquals()
                //.overridingErrorMessage()
                //.describedAs()
                .isEqualTo(expectedList);
    }



}
