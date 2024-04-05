package assertj;

import io.github.mackzwellz.assertj.dto.BaseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectionAssertionsTest extends MyBaseTest {

    @Test
    public void basicReflectionBasedEqualsAndToStringOutput() {
        //TODO allow to assert on lists of top level element
        Assertions.assertThat(actualList.get(0))
                .overridingErrorMessage(
                        BaseDto.customEqualsToString(
                                expectedList.get(0),
                                actualList.get(0),
                                "dto"))
                .isEqualTo(expectedList.get(0));

    }
}
