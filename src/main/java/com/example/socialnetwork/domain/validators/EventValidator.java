package com.example.socialnetwork.domain.validators;

import com.example.socialnetwork.domain.Event;
import com.example.socialnetwork.exceptions.ValidationException;

import java.time.LocalDateTime;

public class EventValidator implements Validator<Event> {

    @Override
    public void validate(Event entity) throws ValidationException {
        String errors = "";
        if (entity == null) {
            errors += "Entity cannot be null\n";
        }
        if (entity.getTitle().equals("")) {
            errors += "Title must not be null\n";
        }
        if (entity.getDate().isBefore(LocalDateTime.now())) {
            errors += "That date has already passed\n";
        }
        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }
}