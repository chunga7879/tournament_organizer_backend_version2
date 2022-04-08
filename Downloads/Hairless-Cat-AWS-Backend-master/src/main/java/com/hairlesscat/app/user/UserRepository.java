package com.hairlesscat.app.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "SELECT * FROM USERS WHERE EMAIL = ?1", nativeQuery = true)
    Optional<User> findUserByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE USERS SET is_admin=?2, first_name=?3, last_name=?4, department=?5, company=?6, email=?7, " +
            "phone_number=?8 WHERE user_id=?1", nativeQuery = true)
    void updateUser(String userId, boolean isAdmin, String FName, String LName, String Department, String Company,
                    String Email, String PNumber);
}
