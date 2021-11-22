package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.PrietenieFileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PrietenieService {
    private final Repository<Tuple<Long, Long>, Prietenie> repo;

    public PrietenieService(Repository<Tuple<Long, Long>, Prietenie> repo) {
        this.repo = repo;
    }

    public Prietenie findOnePrietenie(long l, long l1){
        return repo.findOne(new Tuple<>(l, l1));
    }

    public Prietenie addPrietenie(Prietenie messageTask) {
        return repo.save(messageTask);
    }

    public void removePrietenie(long l, long l1) {
        this.repo.delete(new Tuple<>(l, l1));
    }

    public void updatePrietenie(Prietenie prietenie) {
        repo.update(prietenie);
    }

    public Iterable<Prietenie> getAll() {
        return repo.findAll();
    }

    public Iterable<Prietenie> getAllApproved() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.getStatus().equals("approved"))
                .collect(Collectors.toList());
    }

    public int numarComponenteConexe() {
        return dfs().getLeft();
    }

    public List<Prietenie> celMaiLungDrum() {
        return dfs().getRight();
    }

    public void removePreteniiIfUserIsDeleted(Long id){
        List<Prietenie> prietenieList = new ArrayList<>();
        for (Prietenie prietenie: this.repo.findAll()) {
            if(Objects.equals(prietenie.getId().getLeft(), id) || Objects.equals(prietenie.getId().getRight(), id)){
                prietenieList.add(prietenie);
            }
        }
        for (Prietenie prietenie: prietenieList) {
            this.repo.delete(prietenie.getId());
        }
    }

    private Tuple<Integer, List<Prietenie>> dfs() {
        Iterable<Prietenie> friendships = repo.findAll();
        boolean isEmpty = !friendships.iterator().hasNext();
        if (isEmpty) {
            return new Tuple<>(0, new ArrayList<>());
        }
        int nrCompConexe = 1;
        List<Prietenie> visited = dfsWithoutRecursion(friendships.iterator().next(), friendships);
        List<Prietenie> longestVisited = visited;
        List<Prietenie> globalVisited = new ArrayList<>(visited);
        for (Prietenie prietenie : friendships) {
            if (!globalVisited.contains(prietenie)) {
                visited = dfsWithoutRecursion(prietenie, friendships);
                if (visited.size() > longestVisited.size()) {
                    longestVisited = visited;
                }
                globalVisited.addAll(visited);
                nrCompConexe++;
            }
        }
        return new Tuple<>(nrCompConexe, longestVisited);
    }

    private List<Prietenie> dfsWithoutRecursion(Prietenie first, Iterable<Prietenie> friendships) {
        Stack<Prietenie> stack = new Stack<>();
        List<Prietenie> visited = new ArrayList<>();
        stack.push(first);
        while (!stack.isEmpty()) {
            Prietenie current = stack.pop();
            Long left = current.getId().getLeft();
            Long right = current.getId().getRight();
            if (!visited.contains(current)) {
                visited.add(current);
                for (Prietenie prietenie : friendships) {
                    Long left1 = prietenie.getId().getLeft();
                    if (!visited.contains(prietenie) && (Objects.equals(left1, right) || Objects.equals(left1, left))) {
                        stack.push(prietenie);
                    }
                }
            }
        }
        return visited;
    }

    public List<Prietenie> listaCereriPrietenieUtilizator(Utilizator utilizator){
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.getId().getRight().equals(utilizator.getId())
                            && prietenie.getStatus().equals("pending"))
                .collect(Collectors.toList());
    }
}