package org.example.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.data.entitites.PojistenecEntity;
import org.example.data.entitites.PojisteniEntity;

import java.util.HashMap;

public class PojistenecDTO {
    private String column;
    private String data;
    private long userId;
    private long idVybranehoPojisteni;
    private HashMap<Long, PojisteniEntity> nabidkaPojisteni;
    private boolean hledani;
    private Long id;
    private HashMap<Long, PojistenecEntity> pojistenci;
    @NotBlank(message = "Zadejte jméno")
    @NotNull(message = "Zadejte jméno")
    private String jmeno;
    @NotBlank(message = "Zadejte příjmení")
    @NotNull(message = "Zadejte příjmení")
    private String prijmeni;
    @NotBlank(message = "Zadejte rodné číslo")
    @NotNull(message = "Zadejte rodné číslo")
    private String rodneCislo;
    @NotBlank(message = "Zadejte telefon")
    @NotNull(message = "Zadejte telefon")
    private String telefon;
    @NotBlank(message = "Zadejte ulici a číslo popisné")
    @NotNull(message = "Zadejte ulici a číslo popisné")
    private String uliceCp;
    @NotBlank(message = "Zadejte město")
    @NotNull(message = "Zadejte město")
    private String mesto;
    @NotBlank(message = "Zadejte PSČ")
    @NotNull(message = "Zadejte PSČ")
    private String psc;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getIdVybranehoPojisteni() {
        return idVybranehoPojisteni;
    }

    public void setIdVybranehoPojisteni(long idVybranehoPojisteni) {
        this.idVybranehoPojisteni = idVybranehoPojisteni;
    }

    public HashMap<Long, PojisteniEntity> getNabidkaPojisteni() {
        return nabidkaPojisteni;
    }

    public void setNabidkaPojisteni(HashMap<Long, PojisteniEntity> nabidkaPojisteni) {
        this.nabidkaPojisteni = nabidkaPojisteni;
    }

    public boolean isHledani() {
        return hledani;
    }

    public void setHledani(boolean hledani) {
        this.hledani = hledani;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HashMap<Long, PojistenecEntity> getPojistenci() {
        return pojistenci;
    }

    public void setPojistenci(HashMap<Long, PojistenecEntity> pojistenci) {
        this.pojistenci = pojistenci;
    }

    public long getUserId(){return userId;}
    public void setUserId(long userId){this.userId = userId;}

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
