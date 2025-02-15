package io.github.mackzwellz.assertj.dto;


import io.github.mackzwellz.assertj.util.ReflectionComparatorUtil;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Address extends IdentifiableEntity implements Comparable<Address> {

    private String country;
    private String city;
    private String street;
    private String building;
    private List<Integer> floors = new ArrayList<>();

    @Override
    public Set<String> obtainFieldsToIgnoreInEquals() {
        return Set.of("country", "building");
    }

    @Override
    public int compareTo(Address o) {
        //return this.comparator().compare(this, o);
        return ReflectionComparatorUtil.compareToUsingGetters(this, o, this.obtainGettersForEquals());
    }

}
