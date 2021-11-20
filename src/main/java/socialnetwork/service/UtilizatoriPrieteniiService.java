package socialnetwork.service;

import socialnetwork.domain.Utilizator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UtilizatoriPrieteniiService {
    private final UtilizatorService utilizatorService;
    private final PrietenieService prietenieService;

    public UtilizatoriPrieteniiService(UtilizatorService utilizatorService, PrietenieService prietenieService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
    }

    public void removeUtilizatorAndPrieteniiUtilizator(Long id){
        this.utilizatorService.removeUtilizator(id);
        this.prietenieService.removePreteniiIfUserIsDeleted(id);
    }
}
