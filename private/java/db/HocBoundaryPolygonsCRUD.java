package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import classes.Representative;
import utilities.LogKeeper;

public class HocBoundaryPolygonsCRUD {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public String getFedUidByConstituency(Representative hocRepresentative) {        
        String constituency = hocRepresentative.getConstituency();
        String sql = "SELECT boundary_name, boundary_external_id FROM boundaries_polygons WHERE boundary_name LIKE ? ORDER BY LENGTH(boundary_name) ASC LIMIT 1";
        String fedUid = "";
    
    try (Connection conn = DbManager.getConn();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // Set the parameter with wildcard for LIKE
        stmt.setString(1, "%" + constituency + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String fedName = rs.getString("boundary_name");
            fedUid = rs.getString("boundary_external_id");
            logKeeper.appendLog("FedUid: for " + hocRepresentative.getFirstName() + " " + hocRepresentative.getLastName() + " with constituency " + hocRepresentative.getConstituency() + " <--> " + fedName);
        }

        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        }

        return fedUid;
    }
}
