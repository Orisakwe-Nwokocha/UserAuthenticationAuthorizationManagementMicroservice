package dev.orisha.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.orisha.user_service.config.CustomEmptyStringSerializer;
import dev.orisha.user_service.data.enums.Authority;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
public class UserDTO {

    @JsonProperty("user_id")
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    @JsonSerialize(using = CustomEmptyStringSerializer.class)
    private String password;

    private Set<Authority> authorities;

    private LocalDateTime dateRegistered;

    private LocalDateTime dateUpdated;

}
