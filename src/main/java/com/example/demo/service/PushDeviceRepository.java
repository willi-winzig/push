package com.example.demo.service;

import com.example.demo.entity.Platform;
import com.example.demo.entity.PushDevice;
import org.springframework.data.repository.CrudRepository;

public interface PushDeviceRepository extends CrudRepository<PushDevice, Long> {

    PushDevice findByUserid(Long userid);

    PushDevice findByTokenAndPlatform(String token, Platform platform);
}
