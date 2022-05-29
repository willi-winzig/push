package com.example.demo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class PushOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version private Long version;

    private Long userid;

    @Enumerated(EnumType.STRING)
    private Kategorie kategorie;

    private String param_name;

    private Long param_value;

    @Column(name = "ORDER_TSP")
    private Date ordertsp = new Date();

    @Column(name = "SEND_TSP")
    private Date send;

    private String reason;

    private Date reason_tsp;

    private PushOrder() {}

    public PushOrder(Long userid, Kategorie kategorie, String param_name, Long param_value) {
        this.userid = userid;
        this.kategorie = kategorie;
        this.param_name = param_name;
        this.param_value = param_value;
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

    public String getParam_name() {
        return param_name;
    }

    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public Long getParam_value() {
        return param_value;
    }

    public void setParam_value(Long param_value) {
        this.param_value = param_value;
    }

    public Date getOrdertsp() {
        return ordertsp;
    }

    public void setOrdertsp(Date ordertsp) {
        this.ordertsp = ordertsp;
    }

    public Date getSend() {
        return send;
    }

    public void setSend(Date send) {
        this.send = send;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getReason_tsp() {
        return reason_tsp;
    }

    public void setReason_tsp(Date reason_tsp) {
        this.reason_tsp = reason_tsp;
    }
}
