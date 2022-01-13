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

public class FriendshipService {
    private final Repository<Tuple<Long, Long>, Friendship> repo;

    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> repo) {
        this.repo = repo;
    }

    public Friendship findOneFriendship(long l, long l1) {
        return repo.findOne(new Tuple<>(l, l1));
    }

    public Friendship addFriendship(Friendship friendship) {
        return repo.save(friendship);
    }

    public void removeFriendship(long l, long l1) {
        this.repo.delete(new Tuple<>(l, l1));
    }

    public void updateFriendship(Friendship friendship) {
        repo.update(friendship);
    }

    public Iterable<Friendship> getAll() {
        return repo.findAll();
    }

    public Iterable<Friendship> getAllApproved() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(friendship -> friendship.getStatus().equals("approved"))
                .collect(Collectors.toList());
    }

    public int nrOfConnectedComponents() {
        return dfs().getLeft();
    }

    public List<Friendship> longestRoad() {
        return dfs().getRight();
    }

    public void removeFriendshipsIfUserIsDeleted(Long id) {
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
        int connectedComponents = 1;
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
                connectedComponents++;
            }
        }
        return new Tuple<>(connectedComponents, longestVisited);
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

    public List<Friendship> userFriendRequestsList(User user) {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(friendship -> friendship.getId().getRight().equals(user.getId())
                        && friendship.getStatus().equals("pending"))
                .collect(Collectors.toList());
    }

    public List<Friendship> friendshipsListDuringPeriod(User user, LocalDate st, LocalDate dr){
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(friendship -> (friendship.getId().getLeft().equals(user.getId()) ||
                        friendship.getId().getRight().equals(user.getId()))
                        && friendship.getStatus().equals("approved")
                        && friendship.getDate().toLocalDate().isAfter(st) && friendship.getDate().toLocalDate().isBefore(dr))
                .collect(Collectors.toList());
    }
}