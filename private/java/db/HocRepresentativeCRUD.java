package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import classes.HOCMember;
import classes.Office;
import utilities.Helpers;
import utilities.LogKeeper;

public class HocRepresentativeCRUD {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public boolean insertHOCMemeber(HOCMember hocMember) {
        boolean isInserted = false;
        String sqlRepresentative = "INSERT IGNORE INTO representatives (first_name, last_name, constituency, province_or_territory, political_affiliation, start_date, position, photo_url, boundary_external_id, level, languages, email, url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement stmtRepresemtatives = null;
        PreparedStatement stmtOffices = null;
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
            stmtRepresemtatives.setString(13, hocMember.getUrl());

            int insertedHOCId = stmtRepresemtatives.executeUpdate();
            Helpers.sleep(1);

            if (insertedHOCId > 0) {
                isInserted = true;
                try (ResultSet generatedKeys = stmtRepresemtatives.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        representativeId = generatedKeys.getInt(1);
                        String sqlOffice = "INSERT IGNORE INTO representative_offices (representative_id, type, postal_code, phone, fax) VALUES (?, ?, ?, ?, ?);";
                        if (hocMember.getOffices() != null) {
                            stmtOffices = conn.prepareStatement(sqlOffice);
                            for (int index = 0; index < hocMember.getOffices().size(); index++) {
                                stmtOffices.setInt(1, representativeId);
                                stmtOffices.setString(2, hocMember.getOffices().get(index).getType());
                                stmtOffices.setString(3, hocMember.getOffices().get(index).getPostal());
                                stmtOffices.setString(4, hocMember.getOffices().get(index).getTel());
                                stmtOffices.setString(5, hocMember.getOffices().get(index).getFax());
                                stmtOffices.executeUpdate();
                                Helpers.sleep(1);
                            }
                        }

                        if (hocMember.getRoles() != null) {
                            String sqlRoles = "INSERT IGNORE INTO representative_roles (representative_id, role_name) VALUES (?, ?);";
                            stmtRoles = conn.prepareStatement(sqlRoles);
                            for (int index = 0; index < hocMember.getRoles().size(); index++) {
                                stmtRoles.setInt(1, representativeId);
                                stmtRoles.setString(2, hocMember.getRoles().get(index));
                                stmtRoles.executeUpdate();
                                Helpers.sleep(1);
                            }
                        }

                        logKeeper.appendLog("Inserted representative " + representativeId + ": "
                                + hocMember.getFirstName() + " " + hocMember.getLastName());
                    }
                }
            }
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (stmtRepresemtatives != null) {
                try {
                    stmtRepresemtatives.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }

            if (stmtOffices != null) {
                try {
                    stmtOffices.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }

            if (stmtRoles != null) {
                try {
                    stmtRoles.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
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
            logKeeper.appendLog("Inserted unavilable representative: " + hocMember.getFirstName() + " "
                    + hocMember.getLastName());
            Helpers.sleep(1);
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (stmtUnavilableRepresentative != null) {
                try {
                    stmtUnavilableRepresentative.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
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
            logKeeper.appendLog(e.getMessage());
        }

        return unavailableMembers;
    }

    public void updateUnavilableHOCMember(HOCMember hocMember, boolean added) {
        String sqlUpdate;
        String firstName = hocMember.getFirstName();
        String lastName = hocMember.getLastName();
        if (added) {
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

            logKeeper.appendLog("Updated " + rowsUpdated + " row(s) in the unavilable_representative table for "
                    + firstName + " " + lastName);
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (stmtUpdate != null) {
                try {
                    stmtUpdate.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }
        }
    }

    public List<HOCMember> getHocMembers() {
        logKeeper.appendLog("Reading HOC members from DB");
        String sqlRepresentatives = "SELECT id, first_name, last_name, constituency, province_or_territory, political_affiliation, email, start_date, position, photo_url, boundary_external_id, level, languages, url FROM representatives";
        String sqlOffices = "SELECT type, postal_code, phone, fax FROM representative_offices WHERE representative_id = ?";
        String sqlRoles = "SELECT role_name FROM representative_roles WHERE representative_id = ?";
        List<HOCMember> hocMembers = new ArrayList<>();

        try (Connection conn = DbManager.getConn();
                PreparedStatement stmtRepresentatives = conn.prepareStatement(sqlRepresentatives);
                ResultSet rsRepresentatives = stmtRepresentatives.executeQuery()) {

            while (rsRepresentatives.next()) {
                String firstName = rsRepresentatives.getString("first_name");
                String lastName = rsRepresentatives.getString("last_name");
                String constituency = rsRepresentatives.getString("constituency");
                String provinceOrTerritory = rsRepresentatives.getString("province_or_territory");
                String politicalAffiliation = rsRepresentatives.getString("political_affiliation");
                String email = rsRepresentatives.getString("email");
                String startDate = rsRepresentatives.getString("start_date");
                String position = rsRepresentatives.getString("position");
                String photoUrl = rsRepresentatives.getString("photo_url");
                String boundaryExternalId = rsRepresentatives.getString("boundary_external_id");
                String level = rsRepresentatives.getString("level");
                String languages = rsRepresentatives.getString("languages");
                String url = rsRepresentatives.getString("url");
                HOCMember hocMember = new HOCMember(null, firstName, lastName, constituency, provinceOrTerritory,
                        politicalAffiliation, startDate, null, position, photoUrl, languages, boundaryExternalId, level, email, url);

                // Fetch and set offices
                try (PreparedStatement stmtOffices = conn.prepareStatement(sqlOffices)) {
                    stmtOffices.setInt(1, rsRepresentatives.getInt("id"));
                    try (ResultSet rsOffices = stmtOffices.executeQuery()) {
                        List<Office> offices = new ArrayList<>();
                        while (rsOffices.next()) {
                            String type = rsOffices.getString("type");
                            String postalCode = rsOffices.getString("postal_code");
                            String phone = rsOffices.getString("phone");
                            String fax = rsOffices.getString("fax");
                            offices.add(new Office(fax, phone, type, postalCode));
                        }
                        hocMember.setOffices(offices);
                    }
                }

                // Fetch and set roles
                try (PreparedStatement stmtRoles = conn.prepareStatement(sqlRoles)) {
                    stmtRoles.setInt(1, rsRepresentatives.getInt("id"));
                    try (ResultSet rsRoles = stmtRoles.executeQuery()) {
                        List<String> roles = new ArrayList<>();
                        while (rsRoles.next()) {
                            roles.add(rsRoles.getString("role_name"));
                        }
                        hocMember.setRoles(roles);
                    }
                }

                hocMembers.add(hocMember);
            }

        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        }

        return hocMembers;
    }

    public void updateHocMemberFedUid(HOCMember hocMember) {
        String hocEmail = hocMember.getEmail();
        String hocFedUid = hocMember.getFedUid();
        String sqlUpdate = "UPDATE representatives SET fed_uid = ?  WHERE email = ?;";
        PreparedStatement stmtUpdate = null;
        try (Connection conn = DbManager.getConn()) {
            stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setString(1, hocFedUid);
            stmtUpdate.setString(2, hocEmail);
            int rowsUpdated = stmtUpdate.executeUpdate();

            logKeeper.appendLog("Updated " + rowsUpdated + " row(s) in the representatives table for "
                    + hocMember.getFirstName() + " " + hocMember.getLastName());
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (stmtUpdate != null) {
                try {
                    stmtUpdate.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }
        }
    }
}
