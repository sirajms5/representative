package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import classes.HOCMember;
import utilities.Helpers;
// import utilities.LoggerUtility;

public class RepresentativeCRUD {

    public void insertHOCMemeber(HOCMember hocMember) {
        String sqlRepresentative = "INSERT IGNORE INTO representatives (first_name, last_name, constituency, province_or_territory, political_affiliation, start_date, position, photo_url, boundary_external_id, level, languages) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement stmtRepresemtatives = null;
        PreparedStatement stmtOffices= null;
        PreparedStatement stmtRoles = null;
        int representativeId = 0;

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
                        representativeId = generatedKeys.getInt(1);                        
                        String sqlOffice = "INSERT IGNORE INTO representative_offices (representative_id, type, postal_code, phone, fax) VALUES (?, ?, ?, ?, ?);";
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
                            String sqlRoles = "INSERT IGNORE INTO representative_roles (representative_id, role_name) VALUES (?, ?);";
                            stmtRoles = conn.prepareStatement(sqlRoles);
                            for(int index = 0; index < hocMember.getRoles().size(); index++){
                                stmtRoles.setInt(1, representativeId);
                                stmtRoles.setString(2, hocMember.getRoles().get(index));
                                stmtRoles.executeUpdate();
                                Helpers.sleep(1);
                            }     
                        }     
                        
                        // LoggerUtility.logInfo("Inserted representative number: " + representativeId);
                        System.out.println("Inserted representative " + representativeId + ": " + hocMember.getFirstName() + " " + hocMember.getLastName());
                    }
                }
            }
        } catch (SQLException e) {
            // LoggerUtility.logError(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            if(stmtRepresemtatives != null) {
                try {
                    stmtRepresemtatives.close();
                } catch (SQLException e) {
                    // LoggerUtility.logError(e.getMessage());
                    System.out.println(e.getMessage());
                }
            }

            if(stmtOffices != null) {
                try {
                    stmtOffices.close();
                } catch (SQLException e) {
                    // LoggerUtility.logError(e.getMessage());
                    System.out.println(e.getMessage());
                }
            }

            if(stmtRoles != null) {
                try {
                    stmtRoles.close();
                } catch (SQLException e) {
                    // LoggerUtility.logError(e.getMessage());
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void insertUnavailableRepresentative(HOCMember hocMember) {
        String sqlUnavilableRepresentative = "INSERT INTO unavilable_representative (first_name, last_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE first_name = first_name, last_name = last_name";
        PreparedStatement stmtUnavilableRepresentative = null;

        try (Connection conn = DbManager.getConn()) {
            stmtUnavilableRepresentative = conn.prepareStatement(sqlUnavilableRepresentative);
            stmtUnavilableRepresentative.setString(1, hocMember.getFirstName());
            stmtUnavilableRepresentative.setString(2, hocMember.getLastName());
            stmtUnavilableRepresentative.executeUpdate();
            // LoggerUtility.logInfo("Inserted unavilable representative number: " + hocMember.getFirstName() + " " + hocMember.getLastName());
            System.out.println("Inserted unavilable representative number: " + hocMember.getFirstName() + " " + hocMember.getLastName());
            Helpers.sleep(1);
        } catch (SQLException e) {
            // LoggerUtility.logError(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            if(stmtUnavilableRepresentative != null) {
                try {
                    stmtUnavilableRepresentative.close();
                } catch (SQLException e) {
                    // LoggerUtility.logError(e.getMessage());
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
