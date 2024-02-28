package assertj;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.jupiter.api.Test;

public class DefaultAssertionsTest extends MyBaseTest {

    @Test
    public void defaultTest() {
        //Standard toString
        Assertions.assertThat(actualList.get(0))
                .describedAs("Mismatch between list items")
                .isEqualTo(expectedList.get(0));
    }

    @Test
    public void recursiveComparisonDefaultTest() {
//        //Very close to what we need; list comparison works
//        //TODO override text
        //TODO use overridden equals + text
        //TODO use common set of fields for equals and text
        Assertions.assertThat(actualList.get(0))
                //.overridingErrorMessage() //TODO use me
                .isEqualTo(expectedList.get(0));
    }

    @Test
    public void recursiveComparisonWithOverriddenEqualsTest() {
        //Recursion + overridden equals +-= standard toString
        //not what we need right now. but want to use each object's equals as-is if possible
        Assertions.assertThat(actualList.get(0))
                .usingRecursiveComparison()
                .usingOverriddenEquals()
                .isEqualTo(expectedList.get(0));
    }

    @Test
    public void recursiveSoftAssertionTest() {
        //this is not useful :/
        //only useful if we test against some predicate
        try (AutoCloseableSoftAssertions softAssertions = new AutoCloseableSoftAssertions()) {
            softAssertions.assertThat(actualList)
                    //.usingRecursiveAssertion(new RecursiveAssertionConfiguration())
                    //.withCollectionAssertionPolicy(RecursiveAssertionConfiguration.CollectionAssertionPolicy.COLLECTION_OBJECT_AND_ELEMENTS)
                    .isEqualTo(expectedList);
        }
    }


}
