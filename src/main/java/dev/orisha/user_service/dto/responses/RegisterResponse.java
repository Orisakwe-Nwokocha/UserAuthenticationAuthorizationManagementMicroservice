package dev.orisha.user_service.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterResponse {

    @JsonProperty("user_id")
    private Long id;
    private String email;
    private String message;

}
