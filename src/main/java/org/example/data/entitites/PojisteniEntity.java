package org.example.data.entitites;

import jakarta.persistence.*;

@Entity
public class PojisteniEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "druh")
    private String druh;
    @Column(name = "maximalni_castka")
    private String maximalniCastka;
    @Column(name = "mesicni_splatka")
    private String mesicniCastka;
    private long pocetPojistenych;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDruh() {
        return druh;
    }

    public void setDruh(String druh) {
        this.druh = druh;
    }

    public String getMaximalniCastka() {
        return maximalniCastka;
    }

    public void setMaximalniCastka(String maximalniCastka) {
        this.maximalniCastka = maximalniCastka;
    }

    public String getMesicniCastka() {
        return mesicniCastka;
    }

    public void setMesicniCastka(String mesicniCastka) {
        this.mesicniCastka = mesicniCastka;
    }

    public long getPocetPojistenych() {
        return pocetPojistenych;
    }

    public void setPocetPojistenych(long pocetPojistenych) {
        this.pocetPojistenych = pocetPojistenych;
    }
}
