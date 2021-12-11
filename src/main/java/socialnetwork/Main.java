package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.db.MessagesDbRepository;
import socialnetwork.repository.db.PrieteniiDbRepository;
import socialnetwork.repository.db.UtilizatorDbRepository;
import socialnetwork.service.MesajeService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.service.UtilizatoriPrieteniiService;
import socialnetwork.ui.Console;


public class Main {
    public static void main(String[] args) {

        Validator<Utilizator> validatorUtilizator = new UtilizatorValidator();
        Validator<Prietenie> validatorPrietenie = new PrietenieValidator();

        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        final String password = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");

        Repository<Long, Utilizator> utilizatorDbRepository =
                new UtilizatorDbRepository(url, username, password, validatorUtilizator);
        UtilizatorService utilizatorService = new UtilizatorService(utilizatorDbRepository);

        Repository<Tuple<Long, Long>, Prietenie> prietenieDbRepository =
                new PrieteniiDbRepository(url, username, password, validatorPrietenie);
        PrietenieService prietenieService = new PrietenieService(prietenieDbRepository);

        Repository<Long, Message> mesajeRepository =
                new MessagesDbRepository(url, username, password, null);
        MesajeService mesajeService = new MesajeService(mesajeRepository);

        UtilizatoriPrieteniiService utilizatoriPrieteniiService = new
                UtilizatoriPrieteniiService(utilizatorService, prietenieService, mesajeService);

        Console console = new Console(utilizatorService, prietenieService, utilizatoriPrieteniiService, mesajeService);
        console.run_console();
    }
}