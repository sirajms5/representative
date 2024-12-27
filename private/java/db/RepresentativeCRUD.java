package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import classes.HOCMember;
import utilities.Helpers;

public class RepresentativeCRUD {

    public boolean insertHOCMemeber(HOCMember hocMember) {
        boolean isInserted = false;
        String sqlRepresentative = "INSERT IGNORE INTO representatives (first_name, last_name, constituency, province_or_territory, political_affiliation, start_date, position, photo_url, boundary_external_id, level, languages, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
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
            stmtRepresemtatives.setString(12, hocMember.getEmail());

            int insertedHOCId = stmtRepresemtatives.executeUpdate();
            Helpers.sleep(1);

            if (insertedHOCId > 0) {
                isInserted = true;
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
                        
                        System.out.println("Inserted representative " + representativeId + ": " + hocMember.getFirstName() + " " + hocMember.getLastName());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        return isInserted;
    }

    public void insertUnavailableRepresentative(HOCMember hocMember) {
        String sqlUnavilableRepresentative = "INSERT INTO unavilable_representative (first_name, last_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE first_name = first_name, last_name = last_name";
        PreparedStatement stmtUnavilableRepresentative = null;

        try (Connection conn = DbManager.getConn()) {
            stmtUnavilableRepresentative = conn.prepareStatement(sqlUnavilableRepresentative);
            stmtUnavilableRepresentative.setString(1, hocMember.getFirstName());
            stmtUnavilableRepresentative.setString(2, hocMember.getLastName());
            stmtUnavilableRepresentative.executeUpdate();
            System.out.println("Inserted unavilable representative number: " + hocMember.getFirstName() + " " + hocMember.getLastName());
            Helpers.sleep(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(stmtUnavilableRepresentative != null) {
                try {
                    stmtUnavilableRepresentative.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<HOCMember> getUnavilableHOCMembers() {
        String sql = "SELECT first_name, last_name FROM unavilable_representative WHERE added = 0";
        List<HOCMember> unavailableMembers = new ArrayList<>();

        try (Connection conn = DbManager.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                HOCMember hocMember = new HOCMember(firstName, lastName);
                unavailableMembers.add(hocMember);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unavailableMembers;
    }

    public void updateUnavilableHOCMember(HOCMember hocMember, boolean added) {
        String sqlUpdate;
        String firstName = hocMember.getFirstName();
        String lastName = hocMember.getLastName();
        if(added) {
            sqlUpdate = "UPDATE unavilable_representative SET added = 1 WHERE first_name = ? AND last_name = ?;";
        } else {
            sqlUpdate = "UPDATE unavilable_representative SET added = 1 WHERE first_name = ? AND last_name = ?;";
        }

        PreparedStatement stmtUpdate = null;
        try (Connection conn = DbManager.getConn()) {
            stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setString(1, firstName);
            stmtUpdate.setString(2, lastName);    
            int rowsUpdated = stmtUpdate.executeUpdate();
    
            System.out.println("Updated " + rowsUpdated + " row(s) in the unavilable_representative table for " + firstName + " " + lastName);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmtUpdate != null) {
                try {
                    stmtUpdate.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
