package com.example.socialnetwork.domain.validators;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.exceptions.ValidationException;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";
        String firstName = entity.getFirstName();

        if (firstName == null || firstName.strip().isEmpty()) {
            errors += "First name cannot be null or empty!\n";
        } else {
            if (firstName.charAt(0) < 'A' || firstName.charAt(0) > 'Z') {
                errors += "First name must start with capital letter!\n";
            }
            for (int i = 1; i < firstName.length(); ++i) {
                if (firstName.charAt(i) < 'a' || firstName.charAt(i) > 'z') {
                    errors += "First name must have only letters from a to z! (after the capital letter)\n";
                }
            }
        }

        String lastName = entity.getLastName();
        if (lastName == null || lastName.strip().isEmpty()) {
            errors += "Last name cannot be null or empty!\n";
        } else {
            if (lastName.charAt(0) < 'A' || lastName.charAt(0) > 'Z') {
                errors += "Last name must start with capital letter!\n";
            }
            for (int i = 1; i < lastName.length(); ++i) {
                if (lastName.charAt(i) < 'a' || lastName.charAt(i) > 'z') {
                    errors += "Last name must have only letters from a to z! (after the capital letter)\n";
                }
            }
        }

        if (errors != "") {
            throw new ValidationException(errors);
        }
    }
}
