package com.example.identity_service.dto.request;

import com.example.identity_service.validator.DobContraints;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE )
public class UserUpdateRequest {
    @Size(min=8, message="PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;
    LocalDate dob;

    @DobContraints(min = 18, message = "DOB_INVALID")
    List<String> roles;
}
