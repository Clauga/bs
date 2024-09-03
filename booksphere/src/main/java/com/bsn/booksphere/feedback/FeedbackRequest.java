package com.bsn.booksphere.feedback;

import jakarta.validation.constraints.*;

public record FeedbackRequest(
        //we use record to create a class with only getters and setters, it is a POJO, and a POJO is a class that only has attributes and their getters and setters
        @Positive(message = "200")
        @Min(value = 0, message = "201")
        @Max(value = 5, message = "202")
        Double note,
        //se usa el error 203 para indicar que el comentario es obligatorio
        @NotNull(message = "203")
        @NotEmpty(message = "203")
        @NotBlank(message = "203")
        String comment,
        @NotNull(message = "204")
        Integer bookId
) {
}
