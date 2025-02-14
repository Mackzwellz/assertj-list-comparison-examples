package io.github.mackzwellz.assertj.dto;

import io.github.mackzwellz.assertj.util.ReflectionComparatorUtil;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class User extends IdentifiableEntity implements Comparable<User> {

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

// previous solution for reference:
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof User)) {
//            return false;
//        }
//        User that = (User) o;
//        return ComparatorUtil.equalsUsingGetters(this, that, obtainGettersForEquals());
//    }
//
//    @Override
//    public int hashCode() {
//        int result = super.hashCode();
//        //result = 31 * result + Objects.hashCode(getName());
//        result = 31 * result + Objects.hashCode(getEmail());
//        result = 31 * result + Objects.hashCode(getAddresses());
//        return result;
//    }

    @Override
    public int compareTo(User o) {
        return ReflectionComparatorUtil.compareToUsingGetters(this, o, this.obtainGettersForEquals());
    }
}
