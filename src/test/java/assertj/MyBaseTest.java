package assertj;

import io.github.mackzwellz.assertj.dto.Address;
import io.github.mackzwellz.assertj.dto.User;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyBaseTest {

    static List<User> actualList = new ArrayList<>();
    static List<User> expectedList = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        //List<User> actualList = new ArrayList<>();
        actualList.add(new User("Sherlock", "h@lm.es"));
        actualList.add(new User("John", "w@lm.es"));
        actualList.add(new User("Erqule", "w@lm.es"));
        actualList.add(new User("Marple", "w@lm.es"));

        List<Address> actualAddresses = new ArrayList<>();
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221B"));
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221C"));
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221D"));

        actualList.get(0).setAddresses(actualAddresses);
        actualList.get(1).setAddresses(null);
        actualList.get(2).setAddresses(actualAddresses);
        actualList.get(3).setAddresses(actualAddresses.stream()
                .filter(a -> !a.getBuilding().equals("221D"))
                .collect(Collectors.toList()));


        //List<User> expectedList = new ArrayList<>();
        expectedList.add(new User("Moriarty", "h@lm.es"));
        expectedList.add(new User("Iren", "w@lm.es"));
        expectedList.add(new User("Erqule", "w@lm.es"));
        expectedList.add(new User("Marple", "w@lm.es"));

        List<Address> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(new Address("UK", "Manchester", "Baker st.", "221B"));
        expectedAddresses.add(new Address("UK", "Manchester", "Baker st.", "221C"));

        expectedList.get(0).setAddresses(null);
        expectedList.get(1).setAddresses(expectedAddresses);
        expectedList.get(2).setAddresses(expectedAddresses);
        expectedList.get(3).setAddresses(expectedAddresses);
    }
}
