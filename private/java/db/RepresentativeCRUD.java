package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import classes.HOCMember;
import utilities.Helpers;

public class RepresentativeCRUD {

    public void insertHOCMemeber(HOCMember hocMember) {
        String sqlRepresentative = "INSERT INTO representatives (first_name, last_name, constituency, province_or_territory, political_affiliation, start_date, position, photo_url, boundary_external_id, level, languages) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name), constituency = VALUES(constituency), province_or_territory = VALUES(province_or_territory), political_affiliation = VALUES(political_affiliation), start_date = VALUES(start_date), position = VALUES(position), photo_url = VALUES(photo_url), languages = VALUES(languages);";
        PreparedStatement stmtRepresemtatives = null;
        PreparedStatement stmtOffices= null;
        PreparedStatement stmtRoles = null;

        try (Connection conn = DbManager.getConn()) {
            stmtRepresemtatives = conn.prepareStatement(sqlRepresentative, Statement.RETURN_GENERATED_KEYS);
            stmtRepresemtatives.setString(1, hocMember.getFirstName());
            stmtRepresemtatives.setString(2, hocMember.getLastName());
            stmtRepresemtatives.setString(3, hocMember.getConstituency());
            stmtRepresemtatives.setString(4, hocMember.getProvinceOrTerritory());
            stmtRepresemtatives.setString(5, hocMember.getPoliticalAffiliation());
            stmtRepresemtatives.setString(6, hocMember.getStartDate());
            stmtRepresemtatives.setString(7, hocMember.getPosition());
            stmtRepresemtatives.setString(8, hocMember.getPhotoUrl());
            stmtRepresemtatives.setString(9, hocMember.getBoundaryExternalId());
            stmtRepresemtatives.setString(10, hocMember.getLevel());
            stmtRepresemtatives.setString(11, hocMember.getLanguages());

            int insertedHOCId = stmtRepresemtatives.executeUpdate();
            Helpers.sleep(1);

            if (insertedHOCId > 0) {
                try (ResultSet generatedKeys = stmtRepresemtatives.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int representativeId = generatedKeys.getInt(1);                        
                        String sqlOffice = "INSERT INTO representative_offices (representative_id, type, postal_code, phone, fax) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE type = VALUES(type), postal_code = VALUES(postal_code), phone = VALUES(phone), fax = VALUES(fax);";
                        if(hocMember.getOffices() != null) {
                            stmtOffices = conn.prepareStatement(sqlOffice);
                            for(int index = 0; index < hocMember.getOffices().size(); index++){
                                stmtOffices.setInt(1, representativeId);
                                stmtOffices.setString(2, hocMember.getOffices().get(index).getType());
                                stmtOffices.setString(3, hocMember.getOffices().get(index).getPostal());
                                stmtOffices.setString(4, hocMember.getOffices().get(index).getTel());
                                stmtOffices.setString(5, hocMember.getOffices().get(index).getFax());
                                stmtOffices.executeUpdate();
                                Helpers.sleep(1);
                            }
                        }                        

                        if(hocMember.getRoles() != null) {
                            String sqlRoles = "INSERT INTO representative_roles (representative_id, role_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);";
                            stmtRoles = conn.prepareStatement(sqlRoles);
                            for(int index = 0; index < hocMember.getRoles().size(); index++){
                                stmtRoles.setInt(1, representativeId);
                                stmtRoles.setString(2, hocMember.getRoles().get(index));
                                stmtRoles.executeUpdate();
                                Helpers.sleep(1);
                            }     
                        }     
                        
                        System.out.println("Inserted representative number: " + representativeId);
                    }
                }
            }
        } catch (SQLException e) {

        } finally {
            if(stmtRepresemtatives != null) {
                try {
                    stmtRepresemtatives.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(stmtOffices != null) {
                try {
                    stmtOffices.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(stmtRoles != null) {
                try {
                    stmtRoles.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
