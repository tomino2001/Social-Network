package com.example.socialnetwork.repository.file;

import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrietenieFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Prietenie> {

    public PrietenieFileRepository(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    public Prietenie extractEntity(List<String> attributes) {
        Prietenie prietenie = new Prietenie(Long.parseLong(attributes.get(0)),
                Long.parseLong(attributes.get(1)), LocalDateTime.parse(attributes.get(2), DateTimeFormatter.ISO_DATE_TIME));
        return prietenie;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        String dateTime = entity.getDate().format(DateTimeFormatter.ISO_DATE_TIME);
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + dateTime;
    }
}
