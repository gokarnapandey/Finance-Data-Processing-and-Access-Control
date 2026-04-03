package com.zorvyn.assignment.FinancialRecordManagement.repository;

import com.zorvyn.assignment.FinancialRecordManagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByMobileNumber(String mobileNumber);

    Optional<User> findByUserId(String userId);

    List<User> findAllByIsDeletedTrue();

    Optional<User> findByEmail(String username);
}
