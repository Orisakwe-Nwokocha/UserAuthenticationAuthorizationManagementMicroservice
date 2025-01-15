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

//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder("ErrorResponse{");
//        Field[] fields = this.getClass().getDeclaredFields();
//        boolean firstField = true;
//
//        for (Field field : fields) {
//            field.setAccessible(true);
//            try {
//                Object value = field.get(this);
//                if (value == null) continue;
//
//                if (!firstField) stringBuilder.append(", ");
//                firstField = false;
//
//                boolean isNumber = value instanceof Number;
//                String valueQualifier = isNumber ? "" : "\"";
//                stringBuilder.append("\"")
//                        .append(field.getName())
//                        .append("\": ")
//                        .append(valueQualifier)
//                        .append(value)
//                        .append(valueQualifier);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException("Error accessing field: " + field.getName(), e);
//            }
//        }
//
//        return stringBuilder.append("}").toString();
//    }



    public static void main(String[] args) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setType("error");
        errorResponse.setTitle("title");
        System.out.println(errorResponse);
    }

}
