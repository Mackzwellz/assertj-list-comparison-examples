package io.github.mackzwellz.assertj.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class IdentifiableEntity extends BaseDto {

    private long id;
    private UUID uuid;
    private String description;

}
