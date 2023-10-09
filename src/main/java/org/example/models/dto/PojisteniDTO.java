package org.example.models.dto;

import org.example.data.entitites.PojisteniEntity;

import java.util.HashMap;

public class PojisteniDTO {
    private String column;
    private String data;
    private Long id;
    private String druh;
    private String maximalniCastka;
    private String mesicniSplatka;
    private HashMap<Long, PojisteniEntity> pojisteni;
    private boolean nabidkaJePrazdna;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getMesicniSplatka() {
        return mesicniSplatka;
    }

    public void setMesicniSplatka(String mesicniSplatka) {
        this.mesicniSplatka = mesicniSplatka;
    }

    public HashMap<Long, PojisteniEntity> getPojisteni() {
        return pojisteni;
    }

    public void setPojisteni(HashMap<Long, PojisteniEntity> pojisteni) {
        this.pojisteni = pojisteni;
    }

    public boolean isNabidkaJePrazdna() {
        return nabidkaJePrazdna;
    }

    public void setNabidkaJePrazdna(boolean nabidkaJePrazdna) {
        this.nabidkaJePrazdna = nabidkaJePrazdna;
    }
}
