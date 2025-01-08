package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import classes.Representative;
import classes.Office;
import utilities.Helpers;
import utilities.LogKeeper;
import utilities.RepresentativeLevelEnum;

public class RepresentativeCRUD {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public boolean insertRepresentative(Representative representative) {
        boolean isInserted = false;
        String sqlCheckEmail = "SELECT COUNT(*) FROM representatives WHERE email = ?";
        String sqlRepresentative = "INSERT IGNORE INTO representatives (first_name, last_name, constituency, province_or_territory, political_affiliation, start_date, position, photo_url, boundary_external_id, level, languages, email, url, is_honourable, boundary_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement stmtCheckEmail = null;
        PreparedStatement stmtRepresemtatives = null;
        PreparedStatement stmtOffices = null;
        PreparedStatement stmtRoles = null;
        int representativeId = 0;

        try (Connection conn = DbManager.getConn()) {          
            // Check for duplicate email
            stmtCheckEmail = conn.prepareStatement(sqlCheckEmail);
            stmtCheckEmail.setString(1, representative.getEmail());
            ResultSet rsCheckEmail = stmtCheckEmail.executeQuery();
            if((rsCheckEmail.next() && rsCheckEmail.getInt(1) > 0) || representative.getEmail().equals("")) {
                String newEmailPlaceHolder = generateNewBoundaryId(conn, "Email " + representative.getEmail());
                representative.setEmail(newEmailPlaceHolder);
            }  
            stmtRepresemtatives = conn.prepareStatement(sqlRepresentative, Statement.RETURN_GENERATED_KEYS);
            stmtRepresemtatives.setString(1, representative.getFirstName());
            stmtRepresemtatives.setString(2, representative.getLastName());
            stmtRepresemtatives.setString(3, representative.getConstituency());
            stmtRepresemtatives.setString(4, representative.getProvinceOrTerritory());
            stmtRepresemtatives.setString(5, representative.getPoliticalAffiliation());
            stmtRepresemtatives.setString(6, representative.getStartDate());
            stmtRepresemtatives.setString(7, representative.getPosition());
            stmtRepresemtatives.setString(8, representative.getPhotoUrl());
            stmtRepresemtatives.setString(9, representative.getBoundaryExternalId());
            stmtRepresemtatives.setString(10, representative.getLevel());
            stmtRepresemtatives.setString(11, representative.getLanguages());            
            stmtRepresemtatives.setString(12, representative.getEmail());
            stmtRepresemtatives.setString(13, representative.getUrl());
            stmtRepresemtatives.setBoolean(14, representative.isHonourable());
            stmtRepresemtatives.setString(15, representative.getBoundaryUrl());

            int insertedHOCId = stmtRepresemtatives.executeUpdate();
            Helpers.sleep(1);

            if (insertedHOCId > 0) {
                isInserted = true;
                try (ResultSet generatedKeys = stmtRepresemtatives.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        representativeId = generatedKeys.getInt(1);
                        String sqlOffice = "INSERT IGNORE INTO representative_offices (representative_id, type, postal_code, phone, fax) VALUES (?, ?, ?, ?, ?);";
                        if (representative.getOffices() != null) {
                            stmtOffices = conn.prepareStatement(sqlOffice);
                            for (int index = 0; index < representative.getOffices().size(); index++) {
                                stmtOffices.setInt(1, representativeId);
                                stmtOffices.setString(2, representative.getOffices().get(index).getType());
                                stmtOffices.setString(3, representative.getOffices().get(index).getPostal());
                                stmtOffices.setString(4, representative.getOffices().get(index).getTel());
                                stmtOffices.setString(5, representative.getOffices().get(index).getFax());
                                stmtOffices.executeUpdate();
                                Helpers.sleep(1);
                            }
                        }

                        if (representative.getRoles() != null) {
                            String sqlRoles = "INSERT IGNORE INTO representative_roles (representative_id, role_name) VALUES (?, ?);";
                            stmtRoles = conn.prepareStatement(sqlRoles);
                            for (int index = 0; index < representative.getRoles().size(); index++) {
                                stmtRoles.setInt(1, representativeId);
                                stmtRoles.setString(2, representative.getRoles().get(index));
                                stmtRoles.executeUpdate();
                                Helpers.sleep(1);
                            }
                        }

                        logKeeper.appendLog("Inserted " + representative.getPosition() + " representative "
                                + representativeId + ": "
                                + representative.getFirstName() + " " + representative.getLastName());
                    }
                }
            } else {
                logKeeper.appendLog("Failed to insert: " + representative.getFirstName() + " " + representative.getLastName() + " -- Email: " + representative.getEmail());
            }
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (stmtCheckEmail != null) {
                try {
                    stmtCheckEmail.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }

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

    public void insertUnavailableRepresentativeByFirstAndLastName(Representative representative) {
        String sqlUnavilableRepresentative = "INSERT INTO unavilable_representative (first_name, last_name, position) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE first_name = first_name, last_name = last_name";
        PreparedStatement stmtUnavilableRepresentative = null;

        try (Connection conn = DbManager.getConn()) {
            stmtUnavilableRepresentative = conn.prepareStatement(sqlUnavilableRepresentative);
            stmtUnavilableRepresentative.setString(1, representative.getFirstName());
            stmtUnavilableRepresentative.setString(2, representative.getLastName());
            stmtUnavilableRepresentative.setString(3, representative.getPosition());
            stmtUnavilableRepresentative.executeUpdate();
            logKeeper.appendLog("Inserted unavilable representative: " + representative.getFirstName() + " "
                    + representative.getLastName());
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

    public void insertUnavailableRepresentativeByFullName(Representative representative) {
        String sqlUnavilableRepresentative = "INSERT IGNORE INTO unavilable_representative (full_name, position) VALUES (?, ?)";
        PreparedStatement stmtUnavilableRepresentative = null;

        try (Connection conn = DbManager.getConn()) {
            stmtUnavilableRepresentative = conn.prepareStatement(sqlUnavilableRepresentative);
            stmtUnavilableRepresentative.setString(1, representative.getFullName());
            stmtUnavilableRepresentative.setString(2, representative.getPosition());
            stmtUnavilableRepresentative.executeUpdate();
            logKeeper.appendLog("Inserted unavilable representative: " + representative.getFullName());
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

    public List<Representative> getUnavilableHOCMembers(String position) {
        String sql = "SELECT first_name, last_name FROM unavilable_representative WHERE added = 0 AND position = ?";
        List<Representative> unavailableMembers = new ArrayList<>();

        try (Connection conn = DbManager.getConn();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                Representative representative = new Representative(firstName, lastName, position,
                        RepresentativeLevelEnum.FEDERAL.getValue());
                unavailableMembers.add(representative);
            }

        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        }

        return unavailableMembers;
    }

    public void updateUnavilableHOCMember(Representative representative, boolean added) {
        String sqlUpdate;
        String firstName = representative.getFirstName();
        String lastName = representative.getLastName();
        String position = representative.getPosition();
        if (added) {
            sqlUpdate = "UPDATE unavilable_representative SET added = 1 WHERE first_name = ? AND last_name = ? AND position = ?;";
        } else {
            sqlUpdate = "UPDATE unavilable_representative SET added = 0 WHERE first_name = ? AND last_name = ? AND position = ?;";
        }

        PreparedStatement stmtUpdate = null;
        try (Connection conn = DbManager.getConn()) {
            stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setString(1, firstName);
            stmtUpdate.setString(2, lastName);
            stmtUpdate.setString(3, position);
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

    public List<Representative> getHocMembers(String positionOfMembers) {
        logKeeper.appendLog("Reading HOC members from DB");
        String sqlHocRepresentatives = "SELECT id, first_name, last_name, constituency, province_or_territory, political_affiliation, email, start_date, position, photo_url, boundary_external_id, level, languages, url, is_honourable, boundary_url FROM representatives WHERE position = ?;";
        String sqlOffices = "SELECT type, postal_code, phone, fax FROM representative_offices WHERE representative_id = ?";
        String sqlRoles = "SELECT role_name FROM representative_roles WHERE representative_id = ?";
        List<Representative> hocRepresentatives = new ArrayList<>();
        ResultSet rsHocRepresentatives = null;

        try (Connection conn = DbManager.getConn();
                PreparedStatement stmtHocRepresentatives = conn.prepareStatement(sqlHocRepresentatives);) {

            stmtHocRepresentatives.setString(1, positionOfMembers);
            rsHocRepresentatives = stmtHocRepresentatives.executeQuery();

            while (rsHocRepresentatives.next()) {
                String firstName = rsHocRepresentatives.getString("first_name");
                String lastName = rsHocRepresentatives.getString("last_name");
                String constituency = rsHocRepresentatives.getString("constituency");
                String provinceOrTerritory = rsHocRepresentatives.getString("province_or_territory");
                String politicalAffiliation = rsHocRepresentatives.getString("political_affiliation");
                String email = rsHocRepresentatives.getString("email");
                String startDate = rsHocRepresentatives.getString("start_date");
                String position = rsHocRepresentatives.getString("position");
                String photoUrl = rsHocRepresentatives.getString("photo_url");
                String boundaryExternalId = rsHocRepresentatives.getString("boundary_external_id");
                String level = rsHocRepresentatives.getString("level");
                String languages = rsHocRepresentatives.getString("languages");
                String url = rsHocRepresentatives.getString("url");
                boolean isHonourable = rsHocRepresentatives.getBoolean("is_honourable");
                String boundaryUrl = rsHocRepresentatives.getString("boundary_url");
                Representative hocRepresentative = new Representative(null, firstName, lastName, constituency,
                        provinceOrTerritory,
                        politicalAffiliation, startDate, null, position, photoUrl, languages, boundaryExternalId, level,
                        email, url, isHonourable, boundaryUrl);

                // Fetch and set offices
                try (PreparedStatement stmtOffices = conn.prepareStatement(sqlOffices)) {
                    stmtOffices.setInt(1, rsHocRepresentatives.getInt("id"));
                    try (ResultSet rsOffices = stmtOffices.executeQuery()) {
                        List<Office> offices = new ArrayList<>();
                        while (rsOffices.next()) {
                            String type = rsOffices.getString("type");
                            String postalCode = rsOffices.getString("postal_code");
                            String phone = rsOffices.getString("phone");
                            String fax = rsOffices.getString("fax");
                            offices.add(new Office(fax, phone, type, postalCode));
                        }
                        hocRepresentative.setOffices(offices);
                    }
                }

                // Fetch and set roles
                try (PreparedStatement stmtRoles = conn.prepareStatement(sqlRoles)) {
                    stmtRoles.setInt(1, rsHocRepresentatives.getInt("id"));
                    try (ResultSet rsRoles = stmtRoles.executeQuery()) {
                        List<String> roles = new ArrayList<>();
                        while (rsRoles.next()) {
                            roles.add(rsRoles.getString("role_name"));
                        }
                        hocRepresentative.setRoles(roles);
                    }
                }

                hocRepresentatives.add(hocRepresentative);
            }

        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (rsHocRepresentatives != null) {
                try {
                    rsHocRepresentatives.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }
        }

        return hocRepresentatives;
    }

    public void updateHocMemberFedUid(Representative hocRepresentative) {
        String hocEmail = hocRepresentative.getEmail();
        String hocFedUid = hocRepresentative.getFedUid();
        String sqlUpdate = "UPDATE representatives SET fed_uid = ?  WHERE email = ?;";
        PreparedStatement stmtUpdate = null;
        try (Connection conn = DbManager.getConn()) {
            stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setString(1, hocFedUid);
            stmtUpdate.setString(2, hocEmail);
            int rowsUpdated = stmtUpdate.executeUpdate();

            logKeeper.appendLog("Updated " + rowsUpdated + " row(s) in the representatives table for "
                    + hocRepresentative.getFirstName() + " " + hocRepresentative.getLastName());
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

    public String updateHocMemberBoundaryId(Representative hocRepresentative) {
        String hocEmail = hocRepresentative.getEmail();
        String hocBoundaryExternalId = hocRepresentative.getBoundaryExternalId();
        String sqlUpdate = "UPDATE representatives SET boundary_external_id = ?  WHERE email = ?;";
        String sqlCheck = "SELECT COUNT(*) FROM representatives WHERE boundary_external_id = ?;";
        // String sqlGetLatest = "SELECT latest_boundary_id FROM boundary_id_tracker;";
        // String sqlUpdateTracker = "UPDATE boundary_id_tracker SET latest_boundary_id = ?;";
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtCheck = null;
        // PreparedStatement stmtGetLatest = null;
        // PreparedStatement stmtUpdateTracker = null;
        try (Connection conn = DbManager.getConn()) {
            conn.setAutoCommit(false);
            stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, hocBoundaryExternalId);
            ResultSet rsCheck = stmtCheck.executeQuery();
            if ((rsCheck.next() && rsCheck.getInt(1) > 0) || hocBoundaryExternalId.equals("")) {     
                // stmtGetLatest = conn.prepareStatement(sqlGetLatest);
                // ResultSet rsGetLatest = stmtGetLatest.executeQuery();
                // if (rsGetLatest.next()) {
                //     String latestBoundaryId = rsGetLatest.getString(1);
                //     long newBoundaryId = Long.parseLong(latestBoundaryId) - 1;                    
                //     logKeeper.appendLog("Conflict in boundary_external_id " + hocBoundaryExternalId + " found, generating a new boundary_external_id: " + newBoundaryId);
                //     hocBoundaryExternalId = String.valueOf(newBoundaryId);
                //     hocRepresentative.setBoundaryExternalId(hocBoundaryExternalId);
                //     stmtUpdateTracker = conn.prepareStatement(sqlUpdateTracker);
                //     stmtUpdateTracker.setString(1, hocBoundaryExternalId);
                //     stmtUpdateTracker.executeUpdate();
                // }
                hocBoundaryExternalId = generateNewBoundaryId(conn, hocBoundaryExternalId);
                hocRepresentative.setBoundaryExternalId(hocBoundaryExternalId);
            }

            stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setString(1, hocBoundaryExternalId);
            stmtUpdate.setString(2, hocEmail);
            int rowsUpdated = stmtUpdate.executeUpdate();

            logKeeper.appendLog("Updated " + rowsUpdated + " row(s) in the representatives table for "
                    + hocRepresentative.getFirstName() + " " + hocRepresentative.getLastName());
            conn.commit();
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (stmtCheck  != null) {
                try {
                    stmtCheck .close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }

            // if (stmtGetLatest  != null) {
            //     try {
            //         stmtGetLatest .close();
            //     } catch (SQLException e) {
            //         logKeeper.appendLog(e.getMessage());
            //     }
            // }

            // if (stmtUpdateTracker  != null) {
            //     try {
            //         stmtUpdateTracker .close();
            //     } catch (SQLException e) {
            //         logKeeper.appendLog(e.getMessage());
            //     }
            // }

            if (stmtUpdate  != null) {
                try {
                    stmtUpdate .close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }
        }

        return hocRepresentative.getBoundaryExternalId();
    }

    private String generateNewBoundaryId(Connection conn, String currentBoundaryExternalId) {
        String sqlGetLatest = "SELECT latest_boundary_id FROM boundary_id_tracker;";
        String sqlUpdateTracker = "UPDATE boundary_id_tracker SET latest_boundary_id = ?;";
        PreparedStatement stmtGetLatest = null;
        PreparedStatement stmtUpdateTracker = null;
        String newBoundaryId = null;
    
        try {
            stmtGetLatest = conn.prepareStatement(sqlGetLatest);
            ResultSet rsGetLatest = stmtGetLatest.executeQuery();
    
            if (rsGetLatest.next()) {
                String latestBoundaryId = rsGetLatest.getString(1);
                long newId = Long.parseLong(latestBoundaryId) + 1;
                newBoundaryId = String.valueOf(newId);
                logKeeper.appendLog("Conflict in a unique field " + currentBoundaryExternalId
                        + " found, generating a new boundary_external_id: " + newBoundaryId);
    
                stmtUpdateTracker = conn.prepareStatement(sqlUpdateTracker);
                stmtUpdateTracker.setString(1, newBoundaryId);
                stmtUpdateTracker.executeUpdate();
            }
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {    
            if (stmtGetLatest  != null) {
                try {
                    stmtGetLatest .close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }

            if (stmtUpdateTracker  != null) {
                try {
                    stmtUpdateTracker .close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }
        }
    
        return newBoundaryId;
    }
}
