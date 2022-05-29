package com.example.demo.service;

import com.example.demo.entity.PushOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public interface PushOrderService extends CrudRepository<PushOrder, Long> {

    long countBySendGreaterThan(Date send);

    List<PushOrder> findBySendIsNullOrderByOrdertsp(Pageable pageable);

    @Query(
            value =
                    "UPDATE PushOrder p SET p.send =:date, version=version + 1 WHERE p.id = :id AND send IS NULL")
    @Modifying
    int updatePushOrder(@Param("id") Long id, @Param("date") Date date);
}
