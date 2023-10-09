package org.example.data.entitites;


import jakarta.persistence.*;

@Entity
public class PojistenecEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "jmeno")
    private String jmeno;
    @Column(name = "prijmeni")
    private String prijmeni;
    @Column(name = "rodneCislo")
    private String rodneCislo;
    @Column(name = "telefon")
    private String telefon;
    @Column(name = "uliceCp")
    private String uliceCp;
    @Column(name = "mesto")
    private String mesto;
    @Column(name = "psc")
    private String psc;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public String getRodneCislo() {
        return rodneCislo;
    }

    public void setRodneCislo(String rodneCislo) {
        this.rodneCislo = rodneCislo;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getUliceCp() {
        return uliceCp;
    }

    public void setUliceCp(String uliceCp) {
        this.uliceCp = uliceCp;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }

    public String getPsc() {
        return psc;
    }

    public void setPsc(String psc) {
        this.psc = psc;
    }
}
