package com.apextransport.repository;

import com.apextransport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFirebaseUid(String firebaseUid);
    List<User> findByRole(User.Role role);
    List<User> findByRoleOrderByCreatedAtDesc(User.Role role);
    List<User> findByRoleOrderByPriorityAsc(User.Role role);
    long countByRole(User.Role role);
}
