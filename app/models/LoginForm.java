package models;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Constraints.Validate
public class LoginForm implements Constraints.Validatable<ValidationError> {

    @NotBlank(message = "Username cannot be empty.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contains only letters and numbers(alphanumeric value).")
    String username;

    @NotBlank(message = "Password cannot be empty.")
    String password;

    @Override
    public ValidationError validate() {
        if(!User.validateUser(username, password)) {
            return new ValidationError("username", "Invalid credentials.");
        }
        return null;
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
}
