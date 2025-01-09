package dev.orisha.user_service.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private @NotNull @NotBlank String email;
    private @NotNull @NotBlank String password;

}

