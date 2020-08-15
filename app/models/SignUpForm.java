package models;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.validation.constraints.*;

@Constraints.Validate
public class SignUpForm implements Constraints.Validatable<ValidationError> {
    @NotBlank(message = "Email cannot be empty.")
    @Email
    private String email;

    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contains only letters and numbers(alphanumeric value).")
    private String username;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 8, max = 200, message = "Password must be between 8 and 200 characters.")
    private String password;

    @NotBlank(message = "Password confirmation cannot be empty.")
    private String confirmPassword;

    @Override
    public ValidationError validate() {
        if(!password.equals(confirmPassword)) {
            return new ValidationError("confirmPassword", "Passwords must be the same.");
        }
        return null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
