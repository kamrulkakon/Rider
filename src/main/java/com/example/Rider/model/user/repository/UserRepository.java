package com.example.Rider.model.user.repository;

import com.example.Rider.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmailAndIsDeleted(String email, String isDeleted);

    Boolean existsByEmail(String email);

    Optional<User> findByEmailAndIsDeleted(String email, String isDeleted);

    Optional<User> findByIdAndIsDeleted(long id, String isDeleted);

    Boolean existsByIdAndIsDeleted(long id, String isDeleted);

    @Query("SELECT u FROM User u WHERE u.isDeleted = :isDeleted and (:keyword is null or ((:firstName is null or lower(u.firstName) like %:firstName% or " +
            "lower(u.firstName) like %:keyword%) AND (:middleName is null or lower(u.middleName) like %:middleName% or lower(u.middleName) like %:keyword%) and " +
            "(:lastName is null or lower(u.lastName) like %:lastName% or lower(u.lastName) like %:keyword%) " +
            "or lower(u.email) like %:keyword%)) order by u.createdAt desc")
    Page<User> getUserListWithPagination(
            String keyword,
            String firstName,
            String middleName,
            String lastName,
            String isDeleted,
            Pageable pageable
    );
}
