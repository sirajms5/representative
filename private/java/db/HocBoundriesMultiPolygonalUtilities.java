package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import classes.HocMemberBoundaryPair;
import utilities.LogKeeper;

public class HocBoundriesMultiPolygonalUtilities {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public List<HocMemberBoundaryPair> getHocMemberRepresentativesAndBoundaries(String position) {
        logKeeper.appendLog("Reading HOC members and boundaries joined tables from DB");
        List<HocMemberBoundaryPair> hocMemberBoundaryPairs = new ArrayList<>();
        String sqlQuery = "SELECT representatives.first_name, representatives.last_name, representatives.constituency, representatives.political_affiliation, representatives.province_or_territory, boundaries.external_id, boundaries.shape_url FROM representatives JOIN boundaries ON representatives.boundary_external_id = boundaries.external_id WHERE representatives.position = ?;";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try (Connection conn = DbManager.getConn()) {
            stmt = conn.prepareStatement(sqlQuery);
            stmt.setString(1, position);
            rs = stmt.executeQuery();

            while(rs.next()) {
                String firstName = rs.getString("first_name"); // HOC member first name
                String lastName = rs.getString("last_name"); // HOC member last name
                String constituency = rs.getString("constituency"); // HOC member constituency
                String politicalAffiliation = rs.getString("political_affiliation"); // HOC member political affiliation
                String provinceOrTerritory = rs.getString("province_or_territory"); // HOC member province
                String boundaryExternalId = rs.getString("external_id");
                String fullName = firstName + " " + lastName;
                String shapeUrl = rs.getString("shape_url");
                HocMemberBoundaryPair hocMemberBoundaryPair = new HocMemberBoundaryPair(fullName, constituency, provinceOrTerritory, politicalAffiliation, boundaryExternalId, shapeUrl);
                hocMemberBoundaryPairs.add(hocMemberBoundaryPair);
            }
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }
        }

        return hocMemberBoundaryPairs;
    }
}
