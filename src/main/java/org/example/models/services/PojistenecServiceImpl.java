package org.example.models.services;

import org.example.data.entitites.PojistenecEntity;
import org.example.data.entitites.PojisteniEntity;
import org.example.data.entitites.UserEntity;
import org.example.data.repositories.PojistenecRepository;
import org.example.data.repositories.UserRepository;
import org.example.models.dto.PojistenecDTO;
import org.example.models.exceptions.DuplicateRodneCisloException;
import org.example.models.exceptions.FalseRCException;
import org.example.models.exceptions.SpatnyUdajException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

@Service(value = "pojistenecService")
public class PojistenecServiceImpl implements PojistenecService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PojistenecRepository pojistenecRepository;
    @Override
    public List<Exception> create(PojistenecDTO pojistenecDTO) throws DuplicateRodneCisloException, FalseRCException, SpatnyUdajException {
            List<Exception> errors = new ArrayList<>();
            try {
                rodneCisloAlreadyExists(pojistenecDTO.getRodneCislo());
            }catch (DuplicateRodneCisloException e){
                errors.add(e);
            }
            PojistenecEntity pojistenecEntity = new PojistenecEntity();
            try {
                overUdaje("jmeno", pojistenecDTO.getJmeno());
                pojistenecEntity.setJmeno(osetriUdaje("jmeno", pojistenecDTO.getJmeno()));
            }catch (SpatnyUdajException e){
                errors.add(e);
            }
            try {
                overUdaje("prijmeni", pojistenecDTO.getPrijmeni());
                pojistenecEntity.setPrijmeni(osetriUdaje("prijmeni", pojistenecDTO.getPrijmeni()));
            }catch (SpatnyUdajException e){
                errors.add(e);
            }
            try {
                validateRC(pojistenecDTO.getRodneCislo());
                pojistenecEntity.setRodneCislo(pojistenecDTO.getRodneCislo());
            }catch (FalseRCException e){
                errors.add(e);
            }
            try {
                overUdaje("telefon", pojistenecDTO.getTelefon());
                pojistenecEntity.setTelefon(osetriUdaje("telefon", pojistenecDTO.getTelefon()));
            }catch (SpatnyUdajException e){
                errors.add(e);
            }
            try {
                overUdaje("uliceCp", pojistenecDTO.getUliceCp());
                pojistenecEntity.setUliceCp(pojistenecDTO.getUliceCp());
            }catch (SpatnyUdajException e){
                errors.add(e);
            }
            try {
                overUdaje("mesto", pojistenecDTO.getMesto());
                pojistenecEntity.setMesto(osetriUdaje("mesto", pojistenecDTO.getMesto()));
            }catch (SpatnyUdajException e){
                errors.add(e);
            }
            try {
                overUdaje("psc", pojistenecDTO.getPsc());
                pojistenecEntity.setPsc(pojistenecDTO.getPsc());
            }catch (SpatnyUdajException e){
                errors.add(e);
            }
            if(errors.isEmpty()) {
                pojistenecRepository.save(pojistenecEntity);
                return null;
            }else {
                return errors;
            }
    }
    private void rodneCisloAlreadyExists(String rodneCislo) throws DuplicateRodneCisloException{
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojistenec_entity WHERE rodne_cislo = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setString(1, rodneCislo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                throw new DuplicateRodneCisloException();
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            throw new DuplicateRodneCisloException("Error while checking duplicate rodneCislo", e);
        }
    }
    @Override
    public void update(PojistenecDTO pojistenecDTO) throws SpatnyUdajException{
        String column = pojistenecDTO.getColumn();
        String data = osetriUdaje(column, pojistenecDTO.getData());
        long userId = pojistenecDTO.getUserId();
        long pojistenecId = 0;
        try{
            overUdaje(column, data);
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT id_pojistence FROM uzivatele_pojistenci WHERE id_uzivatele = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                pojistenecId = resultSet.getLong("id_pojistence");
            }
            if(column.equals("jmeno")){
                String updateQuery = "UPDATE pojistenec_entity SET jmeno = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojistenecId);
                statement1.executeUpdate();
                statement1.close();
            }else if(column.equals("prijmeni")){
                String updateQuery = "UPDATE pojistenec_entity SET prijmeni = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojistenecId);
                statement1.executeUpdate();
                statement1.close();
            }else if(column.equals("telefon")){
                String updateQuery = "UPDATE pojistenec_entity SET telefon = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojistenecId);
                statement1.executeUpdate();
                statement1.close();
            }else if(column.equals("uliceCp")){
                String updateQuery = "UPDATE pojistenec_entity SET ulice_cp = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojistenecId);
                statement1.executeUpdate();
                statement1.close();
            }else if(column.equals("mesto")){
                String updateQuery = "UPDATE pojistenec_entity SET mesto = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojistenecId);
                statement1.executeUpdate();
                statement1.close();
            }else if(column.equals("psc")){
                String updateQuery = "UPDATE pojistenec_entity SET psc = ? WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                statement1.setString(1, data);
                statement1.setLong(2, pojistenecId);
                statement1.executeUpdate();
                statement1.close();
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (SpatnyUdajException ex){
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
    @Override
    public void delete(PojistenecDTO pojistenecDTO){
        long userId = pojistenecDTO.getUserId();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT id_pojistence FROM uzivatele_pojistenci WHERE id_uzivatele = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                long pojistenecId = resultSet.getLong("id_pojistence");
                String deleteQuery = "DELETE FROM pojistenec_entity WHERE id = ?";
                PreparedStatement statement1 = connection.prepareStatement(deleteQuery);
                statement1.setLong(1, pojistenecId);
                statement1.executeUpdate();
                statement1.close();

                String deleteQuery1 = "DELETE FROM uzivatele_pojistenci WHERE id_pojistence = ?";
                PreparedStatement statement2 = connection.prepareStatement(deleteQuery1);
                statement2.setLong(1, pojistenecId);
                statement2.executeUpdate();
                statement2.close();

                deletePojistenec(pojistenecDTO);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public long returnPojistenecId(PojistenecDTO pojistenecDTO){
        long userId = pojistenecDTO.getUserId();
        long pojistenecId = 0;
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT id_pojistence FROM uzivatele_pojistenci WHERE id_uzivatele = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                pojistenecId = resultSet.getLong("id_pojistence");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return pojistenecId;
    }
    @Override
    public void savePojistenecToUser(PojistenecDTO pojistenecDTO){
        String rodneCislo = pojistenecDTO.getRodneCislo();
        long userId = pojistenecDTO.getUserId();
        long pojistenecId = 0;
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT id FROM pojistenec_entity WHERE rodne_cislo = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setString(1, rodneCislo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                pojistenecId = resultSet.getLong("id");
            }
            resultSet.close();
            statement.close();

            String insertQuery = "INSERT INTO uzivatele_pojistenci (id_uzivatele, id_pojistence) VALUES (?, ?)";
            PreparedStatement statement1 = connection.prepareStatement(insertQuery);
            statement1.setLong(1, userId);
            statement1.setLong(2, pojistenecId);
            statement1.executeUpdate();
            statement1.close();

            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean dontHavePojistenec(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername()).get();
        long userId = userEntity.getId();
        boolean alreadyExists = true;
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT id_pojistence FROM uzivatele_pojistenci WHERE id_uzivatele = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                alreadyExists = false;
            }else{
                alreadyExists = true;
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return alreadyExists;
    }
    @Override
    public void nactiUdaje(PojistenecDTO pojistenecDTO){
        PojistenecEntity pojistenecEntity = vratPojistenceKUzivateli(pojistenecDTO.getUserId());
        pojistenecDTO.setJmeno(pojistenecEntity.getJmeno());
        pojistenecDTO.setPrijmeni(pojistenecEntity.getPrijmeni());
        pojistenecDTO.setRodneCislo(pojistenecEntity.getRodneCislo());
        pojistenecDTO.setTelefon(pojistenecEntity.getTelefon());
        pojistenecDTO.setUliceCp(pojistenecEntity.getUliceCp());
        pojistenecDTO.setMesto(pojistenecEntity.getMesto());
        pojistenecDTO.setPsc(pojistenecEntity.getPsc());
    }
    private PojistenecEntity vratPojistenceKUzivateli(long userId){
        PojistenecEntity pojistenecEntity = new PojistenecEntity();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojistenec_entity JOIN uzivatele_pojistenci ON pojistenec_entity.id = uzivatele_pojistenci.id_pojistence WHERE uzivatele_pojistenci.id_uzivatele = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                pojistenecEntity.setJmeno(resultSet.getString("jmeno"));
                pojistenecEntity.setPrijmeni(resultSet.getString("prijmeni"));
                pojistenecEntity.setRodneCislo(resultSet.getString("rodne_cislo"));
                pojistenecEntity.setTelefon(resultSet.getString("telefon"));
                pojistenecEntity.setUliceCp(resultSet.getString("ulice_cp"));
                pojistenecEntity.setMesto(resultSet.getString("mesto"));
                pojistenecEntity.setPsc(resultSet.getString("psc"));
            }
            resultSet.close();
            statement.close();
            connection.close();
            return pojistenecEntity;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void deletePojisteniFromPojistenec(PojistenecDTO pojistenecDTO){
        long pojistenecId = returnPojistenecId(pojistenecDTO);
        long pojisteniId = pojistenecDTO.getIdVybranehoPojisteni();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String deleteQuery = "DELETE FROM pojistenci_pojisteni WHERE id_pojistence = ? AND id_pojisteni = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setLong(1, pojistenecId);
            statement.setLong(2, pojisteniId);
            statement.executeUpdate();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public void deletePojistenec(PojistenecDTO pojistenecDTO){
        long pojistenecId = returnPojistenecId(pojistenecDTO);
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String deleteQuery = "DELETE FROM pojistenci_pojisteni WHERE id_pojistence = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setLong(1, pojistenecId);
            statement.executeUpdate();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public void addPojisteniToPojistenec(PojistenecDTO pojistenecDTO){
        long pojistenecId = returnPojistenecId(pojistenecDTO);
        long pojisteniId = pojistenecDTO.getIdVybranehoPojisteni();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String insertQuery = "INSERT INTO pojistenci_pojisteni (id_pojistence, id_pojisteni) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setLong(1, pojistenecId);
            statement.setLong(2, pojisteniId);
            statement.executeUpdate();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public void nactiNabidkuPojisteni(PojistenecDTO pojistenecDTO){
        HashMap<Long, PojisteniEntity> nabidkaPojisteni = new HashMap<>();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojisteni_entity";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                PojisteniEntity pojisteniEntity = new PojisteniEntity();
                pojisteniEntity.setId(resultSet.getLong("id"));
                pojisteniEntity.setDruh(resultSet.getString("druh"));
                pojisteniEntity.setMaximalniCastka(resultSet.getString("maximalni_castka"));
                pojisteniEntity.setMesicniCastka(resultSet.getString("mesicni_splatka"));
                nabidkaPojisteni.put(pojisteniEntity.getId(), pojisteniEntity);
            }
            pojistenecDTO.setNabidkaPojisteni(nabidkaPojisteni);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean haveThisPojisteni(PojistenecDTO pojistenecDTO){
        long pojistenecId = returnPojistenecId(pojistenecDTO);
        long pojisteniId = pojistenecDTO.getIdVybranehoPojisteni();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojistenci_pojisteni WHERE id_pojistence = ? AND id_pojisteni = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, pojistenecId);
            statement.setLong(2, pojisteniId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                resultSet.close();
                statement.close();
                connection.close();
                return true;
            }else{
                resultSet.close();
                statement.close();
                connection.close();
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public PojistenecEntity vratPojistencePodleId(long id){
        PojistenecEntity pojistenecEntity = new PojistenecEntity();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojistenec_entity WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                pojistenecEntity.setId(id);
                pojistenecEntity.setJmeno(resultSet.getString("jmeno"));
                pojistenecEntity.setPrijmeni(resultSet.getString("prijmeni"));
                pojistenecEntity.setRodneCislo(resultSet.getString("rodne_cislo"));
                pojistenecEntity.setTelefon(resultSet.getString("telefon"));
                pojistenecEntity.setUliceCp(resultSet.getString("ulice_cp"));
                pojistenecEntity.setMesto(resultSet.getString("mesto"));
                pojistenecEntity.setPsc(resultSet.getString("psc"));
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return pojistenecEntity;
    }
    @Override
    public void adminUpdate(PojistenecDTO pojistenecDTO) throws SpatnyUdajException{
        String column = pojistenecDTO.getColumn();
        String data = osetriUdaje(column, pojistenecDTO.getData());
        long pojistenecId = pojistenecDTO.getId();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            if(column.equals("jmeno")){
                try {
                    overUdaje("jmeno", data);
                    String updateQuery = "UPDATE pojistenec_entity SET jmeno = ? WHERE id = ?";
                    PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                    statement1.setString(1, data);
                    statement1.setLong(2, pojistenecId);
                    statement1.executeUpdate();
                    statement1.close();
                }catch (SpatnyUdajException e){
                    throw e;
                }
            }else if(column.equals("prijmeni")){
                try {
                    overUdaje("prijmeni", data);
                    String updateQuery = "UPDATE pojistenec_entity SET prijmeni = ? WHERE id = ?";
                    PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                    statement1.setString(1, data);
                    statement1.setLong(2, pojistenecId);
                    statement1.executeUpdate();
                    statement1.close();
                }catch (SpatnyUdajException e){
                    throw e;
                }
            }else if(column.equals("telefon")){
                try {
                    overUdaje("telefon", data);
                    String updateQuery = "UPDATE pojistenec_entity SET telefon = ? WHERE id = ?";
                    PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                    statement1.setString(1, data);
                    statement1.setLong(2, pojistenecId);
                    statement1.executeUpdate();
                    statement1.close();
                }catch (SpatnyUdajException e){
                    throw e;
                }
            }else if(column.equals("uliceCp")){
                try {
                    overUdaje("uliceCp", data);
                    String updateQuery = "UPDATE pojistenec_entity SET ulice_cp = ? WHERE id = ?";
                    PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                    statement1.setString(1, data);
                    statement1.setLong(2, pojistenecId);
                    statement1.executeUpdate();
                    statement1.close();
                }catch (SpatnyUdajException e){
                    throw e;
                }
            }else if(column.equals("mesto")){
                try {
                    overUdaje("mesto", data);
                    String updateQuery = "UPDATE pojistenec_entity SET mesto = ? WHERE id = ?";
                    PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                    statement1.setString(1, data);
                    statement1.setLong(2, pojistenecId);
                    statement1.executeUpdate();
                    statement1.close();
                }catch (SpatnyUdajException e){
                    throw e;
                }
            }else if(column.equals("psc")){
                try {
                    overUdaje("psc", data);
                    String updateQuery = "UPDATE pojistenec_entity SET psc = ? WHERE id = ?";
                    PreparedStatement statement1 = connection.prepareStatement(updateQuery);
                    statement1.setString(1, data);
                    statement1.setLong(2, pojistenecId);
                    statement1.executeUpdate();
                    statement1.close();
                }catch (SpatnyUdajException e){
                    throw e;
                }
            }
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    public void nactiPojistence(PojistenecDTO pojistenecDTO){
        HashMap<Long, PojistenecEntity> pojistenci = new HashMap<>();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM pojistenec_entity";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                PojistenecEntity pojistenecEntity = new PojistenecEntity();
                pojistenecEntity.setId(resultSet.getLong("id"));
                pojistenecEntity.setJmeno(resultSet.getString("jmeno"));
                pojistenecEntity.setPrijmeni(resultSet.getString("prijmeni"));
                pojistenecEntity.setRodneCislo(resultSet.getString("rodne_cislo"));
                pojistenecEntity.setTelefon(resultSet.getString("telefon"));
                pojistenecEntity.setUliceCp(resultSet.getString("ulice_cp"));
                pojistenecEntity.setMesto(resultSet.getString("mesto"));
                pojistenecEntity.setPsc(resultSet.getString("psc"));
                pojistenci.put(pojistenecEntity.getId(), pojistenecEntity);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        pojistenecDTO.setPojistenci(pojistenci);
    }
    @Override
    public void validateRC(String input) throws FalseRCException {
        if(input.length()<=10){
            boolean vstupMaChybu = false;
            for(int i = 0; i < input.length(); i++) {
                if (((int)input.charAt(i) < 48) || ((int)input.charAt(i) > 57)){
                    vstupMaChybu = true;
                    break;
                }
            }
            if(!vstupMaChybu){
                long inputByNumber = Long.parseLong(input);
                if (inputByNumber%11!=0){
                    throw new FalseRCException("RČ není dělitelné 11");
                }
            }else{
                throw new FalseRCException("RČ není ve tvaru čísla");
            }
        }else{
            throw new FalseRCException("Neplatná délka rodného čísla");
        }
    }
    @Override
    public void overUdaje(String column, String udaj) throws SpatnyUdajException{
        String numbers = "0123456789";
        String letters = "abcdefghijklmnopqrstuvwxyzáčďéěíňóřšťúůýžABCDEFGHIJKLMNOPQRSTUVWXYZÁČĎÉĚÍŇÓŘŠŤÚŮÝŽÄÖÜß";
        switch (column){
            case "jmeno":
                String lettersJmeno = "abcdefghijklmnopqrstuvwxyzáčďéěíňóřšťúůýžABCDEFGHIJKLMNOPQRSTUVWXYZÁČĎÉĚÍŇÓŘŠŤÚŮÝŽÄÖÜß -";
                for (int i = 0; i < udaj.length(); i++){
                    boolean obsahuje = false;
                    for (int l = 0; l < lettersJmeno.length(); l++){
                        if (udaj.charAt(i)==lettersJmeno.charAt(l)){
                            obsahuje = true;
                            break;
                        }
                    }
                    if (!obsahuje){
                        throw new SpatnyUdajException("jmeno", "Neplatný formát jména");
                    }
                }
                break;
            case "prijmeni":
                String lettersPrijmeni = "abcdefghijklmnopqrstuvwxyzáčďéěíňóřšťúůýžABCDEFGHIJKLMNOPQRSTUVWXYZÁČĎÉĚÍŇÓŘŠŤÚŮÝŽÄÖÜß -";
                for (int i = 0; i < udaj.length(); i++){
                    boolean obsahuje = false;
                    for (int l = 0; l < lettersPrijmeni.length(); l++){
                        if (udaj.charAt(i)==lettersPrijmeni.charAt(l)){
                            obsahuje = true;
                            break;
                        }
                    }
                    if (!obsahuje){
                        throw new SpatnyUdajException("prijmeni", "Neplatný formát příjmení");
                    }
                }
                break;
            case "telefon":
                if (udaj.length()<9){
                    throw new SpatnyUdajException("telefon", "Neplatný formát telefonního čísla");
                }else {
                    String numbersTelefon = "0123456789+";
                    for (int i = 0; i < udaj.length(); i++) {
                        boolean obsahuje = false;
                        for (int l = 0; l < numbersTelefon.length(); l++) {
                            if (udaj.charAt(i) == numbersTelefon.charAt(l)) {
                                obsahuje = true;
                                break;
                            }
                        }
                        if (!obsahuje) {
                            throw new SpatnyUdajException("telefon", "Neplatný formát telefonního čísla");
                        }
                    }
                }
                break;
            case "uliceCp":
                boolean maCislo = false;
                boolean maPismeno = false;
                boolean maMezeru = false;
                String mezera = " ";
                for (int i = 0; i < udaj.length(); i++){
                    for (int j = 0; j < letters.length(); j++){
                        if(j<numbers.length()){
                            if(udaj.charAt(i)==numbers.charAt(j)){
                                maCislo = true;
                            }
                        }
                        if(udaj.charAt(i)==letters.charAt(j)){
                            maPismeno = true;
                        }
                    }
                    if (udaj.charAt(i)==mezera.charAt(0)){
                        maMezeru = true;
                    }
                }
                if(!maCislo&&maPismeno){throw new SpatnyUdajException("uliceCp", "Neplatný formát adresy - chybí číslo popisné");}
                else if(!maPismeno&&maCislo){throw new SpatnyUdajException("uliceCp", "Neplatný formát adresy - chybí název ulice");}
                else if(!maPismeno&&!maCislo){throw new SpatnyUdajException("uliceCp", "Neplatný formát adresy");}
                else if(maPismeno&&maCislo&&!maMezeru){throw new SpatnyUdajException("uliceCp", "Neplatný formát adresy");}
                break;
            case "mesto":
                for (int i = 0; i < udaj.length(); i++){
                    if ((((int)udaj.charAt(i)) >= 65 && ((int)udaj.charAt(i)) <= 90) || (((int)udaj.charAt(i)) >= 97 && ((int)udaj.charAt(i)) <= 122) || (((int)udaj.charAt(i)) == 45) || (((int)udaj.charAt(i)) == 32)){

                    }else{
                        throw new SpatnyUdajException("mesto", "Neplatný formát města");
                    }
                }
                break;
            case "psc":
                if (udaj.length()==5) {
                    for (int i = 0; i < udaj.length(); i++) {
                        if (((((int) udaj.charAt(i)) >= 48) && (((int) udaj.charAt(i)) <= 57))) {

                        } else {
                            throw new SpatnyUdajException("psc", "Neplatný formát PSČ");
                        }
                    }
                }else{
                    throw new SpatnyUdajException("psc", "Neplatný formát PSČ");
                }
                break;
        }
    }
    private String osetriUdaje(String column, String data){
        if (column.equals("jmeno") || column.equals("prijmeni") || column.equals("mesto")) {
            data = data.trim();
            String prvniPismeno = "";
            prvniPismeno += data.charAt(0);
            prvniPismeno = prvniPismeno.toUpperCase();
            for (int i = 1; i < data.length(); i++) {
                prvniPismeno += data.charAt(i);
            }
            return prvniPismeno;
        }else if (column.equals("telefon")){
            data = data.trim();
            String predvolba = "+420";
            boolean maPredvolbu;
            if (data.charAt(0) == '+'){
                maPredvolbu = true;
            }else{
                maPredvolbu = false;
            }
            if (!maPredvolbu){
                for (int i = 0; i < data.length(); i++){
                    predvolba += data.charAt(i);
                }
                return predvolba;
            }else{
                return data;
            }
        }else{
            data = data.trim();
            return data;
        }
    }
}
