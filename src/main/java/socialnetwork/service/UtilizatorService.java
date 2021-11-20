package socialnetwork.service;

import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;

public class UtilizatorService {
    private final Repository<Long, Utilizator> repo;

    public UtilizatorService(Repository<Long, Utilizator> repo) {
        this.repo = repo;
    }

    public void addUtilizator(Utilizator utilizator) {
        repo.save(utilizator);
    }

    public void removeUtilizator(Long id) {
        repo.delete(id);
    }

    public void updateUtilizator(Utilizator utilizator) {
        repo.update(utilizator);
    }

    public Utilizator findOne(Long id) {
        return repo.findOne(id);
    }

    public Utilizator findByName(String firstName, String lastName) {
        Iterable<Utilizator> utilizatori = repo.findAll();
        Utilizator utilizator = null;
        for (Utilizator u : utilizatori){
            if (u.getFirstName().equals(firstName) && u.getLastName().equals(lastName)){
                utilizator = u;
            }
        }
        return utilizator;
    }

    public Iterable<Utilizator> getAll() {
        return repo.findAll();
    }
}