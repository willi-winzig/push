package com.example.demo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
public class PushCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version private Long version;

    @Enumerated(EnumType.STRING)
    private Kategorie kategorie;

    private Date tsp = new Date();

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private PushDevice pushDevice;

    private PushCategory() {}

    public PushCategory(Kategorie kategorie, PushDevice pushDevice) {
        this.kategorie = kategorie;
        this.pushDevice = pushDevice;
    }

    @Override
    public String toString() {
        return "PushCategory{" + "kategorie='" + kategorie + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PushCategory that = (PushCategory) o;
        return kategorie.equals(that.kategorie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kategorie);
    }

    public Kategorie getKategorie() {
        return kategorie;
    }
}
