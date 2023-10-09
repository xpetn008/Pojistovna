package org.example.models.services;

import org.example.data.entitites.PojisteniEntity;
import org.example.data.repositories.PojisteniRepository;
import org.example.models.dto.PojistenecDTO;
import org.example.models.dto.PojisteniDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;

@Service
public class PojisteniServiceImpl implements PojisteniService{
    @Autowired
    private PojistenecService pojistenecService;
    @Autowired
    private PojisteniRepository pojisteniRepository;
    @Override
    public void create(PojisteniDTO pojisteniDTO) {
        PojisteniEntity pojisteniEntity = new PojisteniEntity();
        pojisteniEntity.setDruh(pojisteniDTO.getDruh());
        pojisteniEntity.setMaximalniCastka(osetriRetezce(pojisteniDTO.getMaximalniCastka()));
        pojisteniEntity.setMesicniCastka(osetriRetezce(pojisteniDTO.getMesicniSplatka()));
        pojisteniRepository.save(pojisteniEntity);
    }
    @Override
    public String vratSoucetMesicnichSplatek(PojistenecDTO pojistenecDTO){
        long pojistenecId = pojistenecService.returnPojistenecId(pojistenecDTO);
        long soucetSplatek = 0;
        String soucetSplatekTextem = "";
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT id_pojisteni FROM pojistenci_pojisteni WHERE id_pojistence = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, pojistenecId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                long pojisteniId = resultSet.getLong("id_pojisteni");
                String selectQuery1 = "SELECT mesicni_splatka FROM pojisteni_entity WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(selectQuery1);
                statement1.setLong(1, pojisteniId);
                ResultSet resultSet1 = statement1.executeQuery();
                if(resultSet1.next()) {
                    String mesicniSplatkaTextem = zmenNaCisla(resultSet1.getString("mesicni_splatka"));
                    long mesicniSplatka = Long.parseLong(mesicniSplatkaTextem);
                    soucetSplatek += mesicniSplatka;
                }
                resultSet1.close();
                statement1.close();
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        soucetSplatekTextem = Long.toString(soucetSplatek);
        return osetriRetezce(soucetSplatekTextem);
    }
    @Override
    public PojisteniEntity vratPojisteniPodleId (Long id){
        PojisteniEntity pojisteniEntity = new PojisteniEntity();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojisteni_entity WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                pojisteniEntity.setDruh(resultSet.getString("druh"));
                pojisteniEntity.setMaximalniCastka(resultSet.getString("maximalni_castka"));
                pojisteniEntity.setMesicniCastka(resultSet.getString("mesicni_splatka"));
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return pojisteniEntity;
    }
    @Override
    public void update(PojisteniDTO pojisteniDTO){
        String column = pojisteniDTO.getColumn();
        String data = osetriRetezce(pojisteniDTO.getData());
        long pojisteniId = pojisteniDTO.getId();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            if(column.equals("druh")){
                String updateQuery = "UPDATE pojisteni_entity SET druh = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojisteniId);
                statement1.executeUpdate();
                statement1.close();
            }else if(column.equals("maximalni_castka")){
                String updateQuery = "UPDATE pojisteni_entity SET maximalni_castka = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojisteniId);
                statement1.executeUpdate();
                statement1.close();
            }else if(column.equals("mesicni_splatka")){
                String updateQuery = "UPDATE pojisteni_entity SET mesicni_splatka = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojisteniId);
                statement1.executeUpdate();
                statement1.close();
            }
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public void nactiVsechnaPojisteni(PojisteniDTO pojisteniDTO){
        HashMap<Long, PojisteniEntity> pojisteni = new HashMap<>();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojisteni_entity";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                PojisteniEntity pojisteniEntity = new PojisteniEntity();
                pojisteniEntity.setId(resultSet.getLong("id"));
                pojisteniEntity.setDruh(resultSet.getString("druh"));
                pojisteniEntity.setMesicniCastka(resultSet.getString("mesicni_splatka"));
                pojisteniEntity.setMaximalniCastka(resultSet.getString("maximalni_castka"));
                String selectQuery1 = "SELECT COUNT(*) AS pocet FROM pojistenci_pojisteni WHERE id_pojisteni = ?";
                PreparedStatement statement1 = connection.prepareStatement(selectQuery1);
                statement1.setLong(1, pojisteniEntity.getId());
                ResultSet resultSet1 = statement1.executeQuery();
                if(resultSet1.next()){
                    pojisteniEntity.setPocetPojistenych(resultSet1.getLong("pocet"));
                }
                pojisteni.put(pojisteniEntity.getId(), pojisteniEntity);
            }
            pojisteniDTO.setPojisteni(pojisteni);
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean nabidkaJePrazdna(){
        boolean nabidkaJePrazdna = true;
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojisteni_entity";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                nabidkaJePrazdna = false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return nabidkaJePrazdna;
    }
    private String osetriRetezce(String input){
        StringBuilder vysledek = new StringBuilder();
        int pocetCislic = 0;
        for (int i = input.length() - 1; i >= 0; i--){
            char c = input.charAt(i);
            vysledek.insert(0, c);
            pocetCislic++;
            if (pocetCislic==3 && i>0){
                vysledek.insert(0, ' ');
                pocetCislic = 0;
            }
        }
        vysledek.append(",- Kč");
        return vysledek.toString();
    }
    private String zmenNaCisla(String input){
        String pouzeCislice = input.replaceAll("[^0-9]", "");
        return pouzeCislice;
    }
}
