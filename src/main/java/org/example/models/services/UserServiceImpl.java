package org.example.models.services;

import jakarta.annotation.PostConstruct;
import org.example.data.entitites.UserEntity;
import org.example.data.repositories.UserRepository;
import org.example.models.dto.UserDTO;
import org.example.models.exceptions.DuplicateEmailException;
import org.example.models.exceptions.PasswordsDoNotEqualException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void create(UserDTO userDTO, boolean isAdmin) throws DuplicateEmailException{
        try{
            if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
                throw new PasswordsDoNotEqualException();
            }
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(userDTO.getEmail());
             userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
             userEntity.setAdmin(isAdmin);
             emailCheck(userEntity.getEmail());
             userRepository.save(userEntity);
        }catch (DuplicateEmailException e){
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username, "+username+" not found"));
    }
    @Override
    public void delete(UserEntity userEntity){
        userRepository.delete(userEntity);
    }
    @Override
    public void changePassword(long id, String newPassword){
        String encodedPassword = passwordEncoder.encode(newPassword);
        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String updateQuery = "UPDATE user_entity SET password = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, encodedPassword);
            statement.setLong(2, id);
            statement.executeUpdate();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void emailCheck(String email) throws DuplicateEmailException{
        boolean emailCheck = true;
        try{
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
            String selectQuery = "SELECT * FROM user_entity WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                emailCheck = false;
            }
            resultSet.close();
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(!emailCheck){
            throw new DuplicateEmailException("Uživatel s tímto emailem je již registrován");
        }
    }
    @PostConstruct
    private void createTables () throws SQLException, DuplicateEmailException {
        Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/pojistovna", "admin", "admin");
        boolean pojistenciPojisteni = false;
        boolean uzivatelePojistenci = false;
        boolean adminExists = false;
        boolean deletePojisteniRelations = false;
        boolean deleteUserRelations = false;
        boolean deletePojistenecRelations = false;

        String pojistenciPojisteniExistence = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = ? AND table_schema = ?)";
        PreparedStatement statement = connection.prepareStatement(pojistenciPojisteniExistence);
        statement.setString(1, "pojistenci_pojisteni");
        statement.setString(2, "pojistovna");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            pojistenciPojisteni = resultSet.getBoolean(1);
        }
        resultSet.close();
        statement.close();

        String uzivatelePojistenciExistence = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = ? AND table_schema = ?)";
        PreparedStatement statement1 = connection.prepareStatement(uzivatelePojistenciExistence);
        statement1.setString(1, "uzivatele_pojistenci");
        statement1.setString(2, "pojistovna");
        ResultSet resultSet1 = statement1.executeQuery();
        if (resultSet1.next()) {
            uzivatelePojistenci = resultSet1.getBoolean(1);
        }
        resultSet1.close();
        statement1.close();

        String adminExistence = "SELECT * FROM user_entity WHERE admin = TRUE";
        PreparedStatement statement4 = connection.prepareStatement(adminExistence);
        ResultSet resultSet4 = statement4.executeQuery();
        if (resultSet4.next()){
            adminExists = true;
        }
        resultSet4.close();
        statement4.close();

        String deletePojisteniRelationsTrigger = "SELECT EXISTS (SELECT 1 FROM information_schema.triggers WHERE trigger_schema = ? AND event_object_table = ? AND trigger_name = ?)";
        PreparedStatement statement5 = connection.prepareStatement(deletePojisteniRelationsTrigger);
        statement5.setString(1, "pojistovna");
        statement5.setString(2, "pojistenec_entity");
        statement5.setString(3, "delete_pojisteni_relations");
        ResultSet resultSet5 = statement5.executeQuery();
        if(resultSet5.next()){
            deletePojisteniRelations = resultSet5.getBoolean(1);
        }
        resultSet5.close();
        statement5.close();

        String deleteUserRelationsTrigger = "SELECT EXISTS (SELECT 1 FROM information_schema.triggers WHERE trigger_schema = ? AND event_object_table = ? AND trigger_name = ?)";
        PreparedStatement statement6 = connection.prepareStatement(deleteUserRelationsTrigger);
        statement6.setString(1, "pojistovna");
        statement6.setString(2, "pojistenec_entity");
        statement6.setString(3, "delete_user_relations");
        ResultSet resultSet6 = statement6.executeQuery();
        if(resultSet6.next()){
            deleteUserRelations = resultSet6.getBoolean(1);
        }
        resultSet6.close();
        statement6.close();

        String deletePojistenecRelationsTrigger = "SELECT EXISTS (SELECT 1 FROM information_schema.triggers WHERE trigger_schema = ? AND event_object_table = ? AND trigger_name = ?)";
        PreparedStatement statement7 = connection.prepareStatement(deletePojistenecRelationsTrigger);
        statement7.setString(1, "pojistovna");
        statement7.setString(2, "pojisteni_entity");
        statement7.setString(3, "delete_pojistenec_relations");
        ResultSet resultSet7 = statement7.executeQuery();
        if(resultSet7.next()){
            deletePojistenecRelations = resultSet7.getBoolean(1);
        }
        resultSet7.close();
        statement7.close();

        if (!pojistenciPojisteni) {
            String createPojistenciPojisteni = "CREATE TABLE pojistenci_pojisteni( " +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_pojistence INT, " +
                    "id_pojisteni INT)";
            PreparedStatement statement2 = connection.prepareStatement(createPojistenciPojisteni);
            statement2.executeUpdate();
            statement2.close();

        }
        if (!uzivatelePojistenci) {
            String createUzivatelePojistenci = "CREATE TABLE uzivatele_pojistenci( " +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_uzivatele INT, " +
                    "id_pojistence INT)";
            PreparedStatement statement3 = connection.prepareStatement(createUzivatelePojistenci);
            statement3.executeUpdate();
            statement3.close();
        }
        if (!deletePojisteniRelations){
            String createTrigger1 =
                    "CREATE TRIGGER delete_pojisteni_relations\n" +
                            "AFTER DELETE ON pojistenec_entity\n" +
                            "FOR EACH ROW\n" +
                            "BEGIN\n" +
                            "    DELETE FROM pojistenci_pojisteni WHERE id_pojistence = OLD.id;\n" +
                            "END;\n";
            PreparedStatement statement8 = connection.prepareStatement(createTrigger1);
            statement8.executeUpdate();
            statement8.close();
        }
        if (!deleteUserRelations){
            String createTrigger2 =
                    "CREATE TRIGGER delete_user_relations\n" +
                            "AFTER DELETE ON pojistenec_entity\n" +
                            "FOR EACH ROW\n" +
                            "BEGIN\n" +
                            "    DELETE FROM uzivatele_pojistenci WHERE id_pojistence = OLD.id;\n" +
                            "END;\n";
            PreparedStatement statement9 = connection.prepareStatement(createTrigger2);
            statement9.executeUpdate();
            statement9.close();
        }
        if (!deletePojistenecRelations){
            String createTrigger3 =
                    "CREATE TRIGGER delete_pojistenec_relations\n" +
                            "AFTER DELETE ON pojisteni_entity\n" +
                            "FOR EACH ROW\n" +
                            "BEGIN\n" +
                            "    DELETE FROM pojistenci_pojisteni WHERE id_pojisteni = OLD.id;\n" +
                            "END;\n";
            PreparedStatement statement10 = connection.prepareStatement(createTrigger3);
            statement10.executeUpdate();
            statement10.close();
        }
        connection.close();

        if (!adminExists) {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail("admin@admin.com");
            userDTO.setPassword("adminpass");
            userDTO.setConfirmPassword("adminpass");
            create(userDTO, true);
        }
    }
}
