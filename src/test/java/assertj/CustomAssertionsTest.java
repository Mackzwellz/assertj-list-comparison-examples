package assertj;

import assertj.custom.CustomAssertWrapper;
import assertj.custom.CustomIgnoringIntrospectionStrategy;
import assertj.custom.CustomRecursiveAssert;
import io.github.mackzwellz.assertj.dto.Address;
import io.github.mackzwellz.assertj.dto.User;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomAssertionsTest extends MyBaseTest {

    @Test
    public void thisIsItTest() {
        //a lot of hoops and dupes for very simple things, but it works.
        CustomAssertWrapper.verifyEquals(actualList, expectedList);

        //Result looks like:

        //java.lang.AssertionError:
        //Equals mismatch for <User> in <ArrayList>:
        //Found 5 differences:
        //
        //field/property '[0].addresses' differ:
        //- actual value  : [Address(country=UK, city=London, street=Baker st., building=221B),
        //    Address(country=UK, city=London, street=Baker st., building=221C),
        //    Address(country=UK, city=London, street=Baker st., building=221D)]
        //- expected value: null
        //
        //field/property '[1].addresses' differ:
        //- actual value  : null
        //- expected value: [Address(country=UK, city=Manchester, street=Baker st., building=221B),
        //    Address(country=UK, city=Manchester, street=Baker st., building=221C)]
        //
        //field/property '[2].addresses' differ:
        //- actual value  : [Address(country=UK, city=London, street=Baker st., building=221B),
        //    Address(country=UK, city=London, street=Baker st., building=221C),
        //    Address(country=UK, city=London, street=Baker st., building=221D)]
        //- expected value: [Address(country=UK, city=Manchester, street=Baker st., building=221B),
        //    Address(country=UK, city=Manchester, street=Baker st., building=221C)]
        //actual and expected values are collections of different size, actual size=3 when expected size=2
        //
        //field/property '[3].addresses[0].city' differ:
        //- actual value  : "London"
        //- expected value: "Manchester"
        //
        //field/property '[3].addresses[1].city' differ:
        //- actual value  : "London"
        //- expected value: "Manchester"
    }

    @Test
    public void thisIsItTest1() {
        List<User> expectedList = new ArrayList<>();
        List<User> actualList = new ArrayList<>();
        actualList.add(new User("Sherlock", "h@lm.es"));

        List<Address> actualAddresses = new ArrayList<>();
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221B", Arrays.asList(1, 2, 3)));
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221C", Arrays.asList(1, 3, 3)));
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221D", Arrays.asList(1, 2)));

        actualList.get(0).setAddresses(actualAddresses);

        expectedList.add(new User("Moriarty", "11111@lm.es"));

        List<Address> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(new Address("UK", "Manchester", "Baker st.", "221B", Arrays.asList(1, 2, 3)));
        expectedAddresses.add(new Address("UK", "Manchester", "Baker st.", "221C", Arrays.asList(1, 2, 3)));

        expectedList.get(0).setAddresses(expectedAddresses);

        CustomAssertWrapper.verifyEquals(actualList, expectedList);
    }

    @Test
    public void thisIsItTest2() {
        List<User> expectedList = new ArrayList<>();
        List<User> actualList = new ArrayList<>();
        actualList.add(new User("Erqule", "w@lm.es"));

        List<Address> actualAddresses = new ArrayList<>();
        actualAddresses.add(new Address("UK", "London", "Parking st.", "221B", Arrays.asList(1, 2, 3)));
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221D", Arrays.asList(1, 2, 1, 2)));

        actualList.get(0).setAddresses(actualAddresses);

        expectedList.add(new User("Marple", "w@lm.es"));

        List<Address> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(new Address("UK", "London", "Baker st.", "221B", Arrays.asList(1, 2, 3)));
        expectedAddresses.add(new Address("UK", "Edinburgh", "Baker st.", "221C", Arrays.asList(1, 2, 3)));

        expectedList.get(0).setAddresses(expectedAddresses);

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
