package com.example.socialnetwork.domain.validators;

import com.example.socialnetwork.domain.Account;
import com.example.socialnetwork.exceptions.ValidationException;

public class AccountValidator implements Validator<Account> {

    @Override
    public void validate(Account entity) throws ValidationException {
        String errors = "";
        if (entity == null) {
            errors += "Account cannot be null";
        }
        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }
}