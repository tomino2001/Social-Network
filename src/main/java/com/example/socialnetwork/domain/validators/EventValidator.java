package com.example.socialnetwork.domain.validators;

import com.example.socialnetwork.domain.Event;
import com.example.socialnetwork.exceptions.ValidationException;

public class EventValidator implements Validator<Event> {

    @Override
    public void validate(Event entity) throws ValidationException {
        String errors = "";
        if (entity == null) {
            errors += "Entity cannot be null\n";
        }
        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }
}