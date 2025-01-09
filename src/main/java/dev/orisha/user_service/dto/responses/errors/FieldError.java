package dev.orisha.user_service.dto.responses.errors;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class FieldError {

    private String objectName;
    private String field;
    private String message;

}
