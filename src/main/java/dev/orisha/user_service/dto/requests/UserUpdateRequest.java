package dev.orisha.user_service.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.orisha.user_service.data.enums.Authority;
import dev.orisha.user_service.dto.UserDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Setter
@Getter
@ToString
@JsonIgnoreProperties({"id", "password", "authorities", "dateRegistered", "dateUpdated"})
public class UserUpdateRequest extends UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String password;
    private Set<Authority> authorities;
    private @NotNull Authority authority;

}
