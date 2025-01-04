package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import classes.HOCMember;
import utilities.LogKeeper;

public class HocBoundaryPolygonsCRUD {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public String getFedUidByConstituency(HOCMember hocMember) {        
        String constituency = hocMember.getConstituency();
        String sql = "SELECT fedname, feduid FROM boundaries_polygons WHERE fedname LIKE ? ORDER BY LENGTH(fedname) ASC LIMIT 1";
        String fedUid = "";
    
    try (Connection conn = DbManager.getConn();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // Set the parameter with wildcard for LIKE
        stmt.setString(1, "%" + constituency + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String fedName = rs.getString("fedname");
            fedUid = rs.getString("feduid");
            logKeeper.appendLog("FedUid: for " + hocMember.getFirstName() + " " + hocMember.getLastName() + " with constituency " + hocMember.getConstituency() + " <--> " + fedName);
        }

        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        }

        return fedUid;
    }
}
