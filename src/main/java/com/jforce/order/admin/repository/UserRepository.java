package com.jforce.order.admin.repository;

import com.jforce.order.admin.entity.JForceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<JForceUser, Long> {

    Optional<JForceUser> findByUsername(String username);
}
