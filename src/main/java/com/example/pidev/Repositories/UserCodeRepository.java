package com.example.pidev.Repositories;

import com.example.pidev.Entities.User;
import com.example.pidev.Entities.UserCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCodeRepository extends JpaRepository<UserCode,Long> {
    UserCode findByCode(String code);
}
