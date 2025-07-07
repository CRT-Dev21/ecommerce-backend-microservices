package com.example.gateway_service.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "The name cannot be empty")
    @Size(min = 2, max = 50, message = "The name must be between 2 and 50 characters")
    private String name;

    @Email(message = "The email format is not valid")
    @NotBlank(message = "The email cannot be empty")
    private String email;

    @NotBlank(message = "The password cannot be empty")
    @Size(min = 6, max = 20, message = "The name must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "The role cannot be empty")
    private String role;
}
