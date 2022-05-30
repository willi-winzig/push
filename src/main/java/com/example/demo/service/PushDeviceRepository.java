package com.example.demo.service;

import com.example.demo.entity.Platform;
import com.example.demo.entity.PushDevice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PushDeviceRepository extends CrudRepository<PushDevice, Long> {

    PushDevice findByUserid(Long userid); // LAZY

    @Query(value = "Select p FROM PushDevice p JOIN FETCH p.categories c where p.userid = :userid")
    PushDevice findPushDevice(@Param("userid") Long userid);  // EAGER

    PushDevice findByTokenAndPlatform(String token, Platform platform);
}
