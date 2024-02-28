package assertj;

import io.github.mackzwellz.assertj.dto.Address;
import io.github.mackzwellz.assertj.dto.User;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;

public class MyBaseTest {

    static List<User> actualList = new ArrayList<>();
    static List<User> expectedList = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        //List<User> actualList = new ArrayList<>();
        actualList.add(new User("Sherlock", "h@lm.es"));
        actualList.add(new User("John", "w@lm.es"));

        List<Address> actualAddresses = new ArrayList<>();
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221B"));
        actualAddresses.add(new Address("UK", "London", "Baker st.", "221C"));

        actualList.get(0).setAddresses(actualAddresses);
        actualList.get(1).setAddresses(actualAddresses);

        //List<User> expectedList = new ArrayList<>();
        expectedList.add(new User("Moriarty", "h@lm.es"));
        expectedList.add(new User("Iren", "w@lm.es"));

        List<Address> expectedAddresses = new ArrayList<>();
        expectedAddresses.add(new Address("UK", "Manchester", "Baker st.", "221B"));
        expectedAddresses.add(new Address("UK", "Manchester", "Baker st.", "221C"));

        expectedList.get(0).setAddresses(expectedAddresses);
        expectedList.get(1).setAddresses(actualAddresses);
    }
}
