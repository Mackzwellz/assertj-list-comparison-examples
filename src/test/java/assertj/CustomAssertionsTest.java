package assertj;

import assertj.custom.CustomIgnoringIntrospectionStrategy;
import assertj.custom.CustomRecursiveAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
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

    @Test
    public void customRecursiveAssertTest() {
        // same as //.usingRecursiveComparison()
        CustomRecursiveAssert ass = new CustomRecursiveAssert<>(actualList, new RecursiveComparisonConfiguration());

        // Assertions.assertThat(termsOfSaleCSUtil.actualTermsOfSaleDto)
        ass
                //.usingRecursiveComparison()
                //.usingComparator(new RecursiveComparator(new RecursiveComparisonConfiguration()))
                //TODO custom formatting for deep equals regardless of overridden equals! need to find the entrypoint for formatter
                //.usingOverriddenEquals() // useless since output doesn't reflect individual fields

                // either use overridden or below logic
                //TODO replace with getter of fieldslist; implement methods to recursively collect all ignored fields in proper format like "owners.dataEntryDate"!
                .ignoringFields("created_date", "modified_date", "version") //top level only for now
                .ignoringFieldsMatchingRegexes(".*LOG") //class type doesn't work -> no getter; either remove static logger or keep this filter
                .ignoringCollectionOrder() //you must have everything as lists, but they can be in any order as long as objects match 1-to-1?
                //.isEqualTo(termsOfSaleCSUtil.expectedTermsOfSaleDto);
                .customTextIsEqualTo(expectedList);

    }





}
