package io.github.mackzwellz.assertj.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Address extends IdentifiableEntity {

    private String country;
    private String city;
    private String street;
    private String building;

}
