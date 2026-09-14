package com.rksolutions.user.repository;

import com.rksolutions.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String emailOrPhone, String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByStatus(com.rksolutions.common.enums.UserStatus status);
}
