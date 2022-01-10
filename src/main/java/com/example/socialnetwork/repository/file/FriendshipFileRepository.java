package com.example.socialnetwork.repository.file;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {

    public FriendshipFileRepository(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        Friendship friendship = new Friendship(Long.parseLong(attributes.get(0)),
                Long.parseLong(attributes.get(1)), LocalDateTime.parse(attributes.get(2), DateTimeFormatter.ISO_DATE_TIME));
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        String dateTime = entity.getDate().format(DateTimeFormatter.ISO_DATE_TIME);
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + dateTime;
    }
}
