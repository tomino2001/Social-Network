package com.example.socialnetwork.domain.validators;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.exceptions.ValidationException;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        String errors = "";
        if (entity == null)
            errors += "Friendship cannot be null!\n";
        if (!errors.equals(""))
            throw new ValidationException(errors);
    }
}