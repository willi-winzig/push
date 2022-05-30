package com.example.demo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
public class PushOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private Long userid;

    @Enumerated(EnumType.STRING)
    private Kategorie kategorie;

    private String payload;

    private Date expiration;

    private Date send;

    private PushOrder() {
    }

    public PushOrder(Long userid, Kategorie kategorie, String payload) {
        this.userid = userid;
        this.kategorie = kategorie;
        this.payload = payload;
        Instant expireAt = new Date().toInstant();
        this.expiration = Date.from(expireAt.plus(3, ChronoUnit.DAYS));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Kategorie getKategorie() {
        return kategorie;
    }

    public void setKategorie(Kategorie kategorie) {
        this.kategorie = kategorie;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getSend() {
        return send;
    }

    public void setSend(Date send) {
        this.send = send;
    }

    public boolean hasExpired() {
        return expiration.before(new Date());
    }
}
