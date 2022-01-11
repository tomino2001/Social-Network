package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.repository.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipsService {
    private final Repository<Tuple<Long, Long>, Friendship> repo;

    public FriendshipsService(Repository<Tuple<Long, Long>, Friendship> repo) {
        this.repo = repo;
    }

    public Friendship findOnePrietenie(long l, long l1) {
        return repo.findOne(new Tuple<>(l, l1));
    }

    public Friendship addPrietenie(Friendship messageTask) {
        return repo.save(messageTask);
    }

    public void removePrietenie(long l, long l1) {
        this.repo.delete(new Tuple<>(l, l1));
    }

    public void updatePrietenie(Friendship friendship) {
        repo.update(friendship);
    }

    public Iterable<Friendship> getAll() {
        return repo.findAll();
    }

    public Iterable<Friendship> getAllApproved() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.getStatus().equals("approved"))
                .collect(Collectors.toList());
    }

    public int numarComponenteConexe() {
        return dfs().getLeft();
    }

    public List<Friendship> celMaiLungDrum() {
        return dfs().getRight();
    }

    public void removePreteniiIfUserIsDeleted(Long id) {
        List<Friendship> friendshipList = new ArrayList<>();
        for (Friendship friendship : this.repo.findAll()) {
            if (Objects.equals(friendship.getId().getLeft(), id) || Objects.equals(friendship.getId().getRight(), id)) {
                friendshipList.add(friendship);
            }
        }
        for (Friendship friendship : friendshipList) {
            this.repo.delete(friendship.getId());
        }
    }

    private Tuple<Integer, List<Friendship>> dfs() {
        Iterable<Friendship> friendships = getAllApproved();
        boolean isEmpty = !friendships.iterator().hasNext();
        if (isEmpty) {
            return new Tuple<>(0, new ArrayList<>());
        }
        int nrCompConexe = 1;
        List<Friendship> visited = dfsWithoutRecursion(friendships.iterator().next(), friendships);
        List<Friendship> longestVisited = visited;
        List<Friendship> globalVisited = new ArrayList<>(visited);
        for (Friendship friendship : friendships) {
            if (!globalVisited.contains(friendship)) {
                visited = dfsWithoutRecursion(friendship, friendships);
                if (visited.size() > longestVisited.size()) {
                    longestVisited = visited;
                }
                globalVisited.addAll(visited);
                nrCompConexe++;
            }
        }
        return new Tuple<>(nrCompConexe, longestVisited);
    }

    private List<Friendship> dfsWithoutRecursion(Friendship first, Iterable<Friendship> friendships) {
        Stack<Friendship> stack = new Stack<>();
        List<Friendship> visited = new ArrayList<>();
        stack.push(first);
        while (!stack.isEmpty()) {
            Friendship current = stack.pop();
            Long left = current.getId().getLeft();
            Long right = current.getId().getRight();
            if (!visited.contains(current)) {
                visited.add(current);
                for (Friendship friendship : friendships) {
                    Long left1 = friendship.getId().getLeft();
                    if (!visited.contains(friendship) && (Objects.equals(left1, right) || Objects.equals(left1, left))) {
                        stack.push(friendship);
                    }
                }
            }
        }
        return visited;
    }

    public List<Friendship> listaCereriPrietenieUtilizator(User user) {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.getId().getRight().equals(user.getId())
                        && prietenie.getStatus().equals("pending"))
                .collect(Collectors.toList());
    }

    public List<Friendship> listaCereriPrietenieUtilizatorALL(User user) {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.getId().getRight().equals(user.getId())
                        || prietenie.getId().getLeft().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public List<Friendship> listaPrieteniiDinPerioadaX(User user, LocalDate st, LocalDate dr){
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(prietenie -> (prietenie.getId().getLeft().equals(user.getId()) ||
                        prietenie.getId().getRight().equals(user.getId()))
                        && prietenie.getStatus().equals("approved")
                        && prietenie.getDate().toLocalDate().isAfter(st) && prietenie.getDate().toLocalDate().isBefore(dr))
                .collect(Collectors.toList());
    }
}