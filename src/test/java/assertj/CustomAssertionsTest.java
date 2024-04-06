package assertj;

import assertj.custom.CustomAssertWrapper;
import assertj.custom.CustomIgnoringIntrospectionStrategy;
import assertj.custom.CustomRecursiveAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;

public class CustomAssertionsTest extends MyBaseTest {

    @Test
    public void thisIsItTest() {
        //a lot of hoops and dupes for very simple things, but it works.
        CustomAssertWrapper.verifyEquals(actualList, expectedList);
    }

    @Test
    public void recursiveComparisonWithCustomEqualsTest() {
        // custom introspection strategy solves equals problem AND assertion text string problem!!
        // another option would be to use conditions / verbose conditions, as seen in https://policyexpert-engineering.co.uk/precise-and-readable-assertions-with-assertj-ce50402d6dcb
        // now we only need to find how to reformat default message
        // and test for difference in amount of data
        Assertions.assertThat(actualList)
                .usingRecursiveComparison()
                .withIntrospectionStrategy(new CustomIgnoringIntrospectionStrategy()) //ignores names of User
                //.overridingErrorMessage() <- solves custom message but results in a lot of duplication
                //.withRepresentation() // <- custom tostring for objects for actual/expected diff
                //.usingOverriddenEquals() // <- solved by introspection strategy
                //.describedAs() // <- can add custom message before main assertion content
                .isEqualTo(expectedList);
    }

    @Test
    //this is ass indeed, not useful
    public void customRecursiveAssertTest() {
        //should solve custom assertion text problem -
        // we need to be able to exclude fields that we don't compare
        // from assertion text string

        // same as //.usingRecursiveComparison()
        CustomRecursiveAssert ass = new CustomRecursiveAssert<>(actualList, new RecursiveComparisonConfiguration());

        // Assertions.assertThat(actualList.get(0))
        ass
                .usingRecursiveComparison1()
                //.usingComparator(new RecursiveComparator(new RecursiveComparisonConfiguration()))
                //TODO custom formatting for deep equals regardless of overridden equals! need to find the entrypoint for formatter
                //.usingOverriddenEquals() // useless since output doesn't reflect individual fields

                // either use overridden or below logic
                //TODO replace with getter of fieldslist; implement methods to recursively collect all ignored fields in proper format like "owners.dataEntryDate"!
                .ignoringFields("created_date", "modified_date", "version") //top level only for now
                .ignoringFieldsMatchingRegexes(".*LOG") //class type doesn't work -> no getter; either remove static logger or keep this filter
                .ignoringCollectionOrder() //you must have everything as lists, but they can be in any order as long as objects match 1-to-1?
                //.isEqualTo(expectedList.get(0);
                .customTextIsEqualTo(expectedList);

    }


}
