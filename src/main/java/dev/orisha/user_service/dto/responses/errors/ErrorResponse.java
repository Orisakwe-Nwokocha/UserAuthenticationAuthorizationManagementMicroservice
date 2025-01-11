package dev.orisha.user_service.dto.responses.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(pattern = "E, dd-MMMM-yyyy 'at' hh:mm a")
    private LocalDateTime timestamp;
    private String type;
    private String title;
    private int status;
    private String error;
    private String detail;
    private String path;
    private String message;
    private List<FieldError> fieldErrors;

}
