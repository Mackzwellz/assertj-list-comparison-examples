package io.github.mackzwellz.assertj.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BaseDto {

    public Set<String> obtainFieldsToIgnoreInEquals() {
        return Collections.emptySet();
    }
}
