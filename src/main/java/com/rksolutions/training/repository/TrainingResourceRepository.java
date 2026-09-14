package com.rksolutions.training.repository;

import com.rksolutions.training.entity.TrainingResource;
import com.rksolutions.common.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingResourceRepository extends JpaRepository<TrainingResource, Long> {
    List<TrainingResource> findByActiveTrue();

    List<TrainingResource> findByResourceType(ResourceType resourceType);

    List<TrainingResource> findByCategory(String category);
}
