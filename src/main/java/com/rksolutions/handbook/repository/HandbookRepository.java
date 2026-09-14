package com.rksolutions.handbook.repository;

import com.rksolutions.handbook.entity.Handbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HandbookRepository extends JpaRepository<Handbook, Long> {
    List<Handbook> findByActiveTrue();

    List<Handbook> findByCategory(String category);
}
