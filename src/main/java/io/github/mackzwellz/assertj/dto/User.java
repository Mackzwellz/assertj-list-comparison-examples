package io.github.mackzwellz.assertj.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class User extends IdentifiableEntity {

    private String name;
    private String email;
    private List<Address> addresses;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public Set<String> obtainFieldsToIgnoreInEquals() {
        return Set.of("name");
    }

    @Override
    public List<Method> obtainGettersForEquals() {
        List<String> excludedGetters = List.of(
                "name"
        );
        return obtainAllGettersExceptFor(excludedGetters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User that = (User) o;

        //LOG.info("Verifying fields for {}/{}", );
        return equalsUsingGetters(this, that, obtainGettersForEquals());
    }
}
