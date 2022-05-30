package com.example.demo.entity;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class PushDevice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @NaturalId
    private Long userid;

    @Column(name = "token", length = 100)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 100)
    private Platform platform;

    private String public_key;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "pushDevice",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private Set<PushCategory> categories = new HashSet<>();

    private Date tsp = new Date();

    private PushDevice() {
    }

    public PushDevice(Long userid) {
        this.userid = userid;
    }

    public PushDevice(Long userid, String token, Platform platform, String public_key) {
        this.userid = userid;
        this.token = token;
        this.platform = platform;
        this.public_key = public_key;
    }

    public Long getUserid() {
        return userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public Set<PushCategory> getCategories() {
        return categories;
    }

    public Set<String> getCategoriesAsStringSet() {
        Set<String> set = new HashSet<>();
        for (PushCategory c : getCategories()) {
            set.add(c.getKategorie().name());
        }
        return set;

    }

    public Set<Kategorie> getKategorien() {
        Set<Kategorie> set = new HashSet<>();
        for (PushCategory c : getCategories()) {
            set.add(c.getKategorie());
        }
        return set;

    }

    @Override
    public String toString() {
        return "PushDevice{"
                + "userid="
                + userid
                + ", token='"
                + token
                + '\''
                + ", platform="
                + platform
                + ", public_key='"
                + public_key
                + '\''
                + ", categories="
                + categories
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PushDevice that = (PushDevice) o;
        return userid.equals(that.userid)
                && token.equals(that.token)
                && platform == that.platform
                && public_key.equals(that.public_key)
                && categories.equals(that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, token, platform, public_key, categories);
    }
}
