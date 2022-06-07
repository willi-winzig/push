package com.example.demo.service;

import com.example.demo.entity.Kategorie;
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
public interface PushOrderRepository extends CrudRepository<PushOrder, Long> {

    long countBySendGreaterThan(Date send);

    List<PushOrder> findBySendIsNullOrderByExpiration(Pageable pageable);

    @Query(
            value =
                    "UPDATE PushOrder p SET p.send =:date, p.version = p.version + 1 WHERE p.id = :id AND p.send IS NULL")
    @Modifying
    int updatePushOrder(@Param("id") Long id, @Param("date") Date date);

    @Query(
            value =
                    "DELETE FROM PushOrder p WHERE p.userid = :userid AND p.send IS NULL")
    @Modifying
    long deletePushOrder(@Param("userid") Long userid);

    @Query(
            value =
                    "DELETE FROM PushOrder p WHERE p.userid = :userid AND p.kategorie = :kategorie AND p.send IS NULL")
    @Modifying
    long deletePushOrder(@Param("userid") Long userid, @Param("kategorie") Kategorie kategorie);
}
