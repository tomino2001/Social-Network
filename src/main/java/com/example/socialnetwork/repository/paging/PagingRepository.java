package com.example.socialnetwork.repository.paging;


import com.example.socialnetwork.domain.Entity;
import com.example.socialnetwork.repository.Repository;

public interface PagingRepository<ID,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}