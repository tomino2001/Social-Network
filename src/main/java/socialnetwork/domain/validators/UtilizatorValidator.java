package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String firstName = entity.getFirstName().trim();
        if (firstName == null || firstName.isEmpty() || firstName.charAt(0) < 'A' || firstName.charAt(0) > 'Z') {
            throw new ValidationException("Invalid First Name");
        }
    }
}
