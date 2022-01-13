package com.example.socialnetwork.domain.validators;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.exceptions.ValidationException;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        String errors = "";
        if (entity == null)
            errors += "Message can't be null\n";
        if (entity.getContent() == "")
            errors += "Message content cannot be empty\n";
        if (!errors.equals(""))
            throw new ValidationException(errors);
    }
}