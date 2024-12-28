package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import classes.Boundary;
import classes.HOCMember;
import utilities.Helpers;
import utilities.LogKeeper;

public class HocBoundariesCRUD {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void insertUnavilableBoundary(HOCMember hocMember) {
        String sqlUnavilableBoundary = "INSERT IGNORE INTO unavilable_hoc_boundary (boundary_external_id) VALUES (?);";
        PreparedStatement stmtUnavilableRepresentative = null;

        try (Connection conn = DbManager.getConn()) {
            stmtUnavilableRepresentative = conn.prepareStatement(sqlUnavilableBoundary);
            stmtUnavilableRepresentative.setString(1, hocMember.getBoundaryExternalId());
            stmtUnavilableRepresentative.executeUpdate();
            logKeeper.appendLog("Inserted unavilable HOC boundary number: " + hocMember.getBoundaryExternalId());
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

    public void insertUnavilableSimpleShape(String boundaryExternalId, String simpleShapeUrl) {
        String sqlUnavilableSimpleShape = "INSERT IGNORE INTO unavilable_hoc_simple_shape (boundary_external_id, simple_shape_url) VALUES (?, ?);";
        PreparedStatement stmtUnavilableRepresentative = null;

        try (Connection conn = DbManager.getConn()) {
            stmtUnavilableRepresentative = conn.prepareStatement(sqlUnavilableSimpleShape);
            stmtUnavilableRepresentative.setString(1, boundaryExternalId);
            stmtUnavilableRepresentative.setString(2, simpleShapeUrl);
            stmtUnavilableRepresentative.executeUpdate();
            logKeeper.appendLog("Inserted unavilable simple shape boundary number: " + boundaryExternalId
                    + ". url: " + simpleShapeUrl);
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

    public boolean insertBoundary(Boundary boundary) {
        boolean isInserted = false;
        String sqlBoundaries = "INSERT IGNORE INTO boundaries (external_id, boundary_name, min_latitude, max_latitude, min_longitude, max_longitude, simple_shape_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmtBoundaries = null;
        try (Connection conn = DbManager.getConn()) {
            stmtBoundaries = conn.prepareStatement(sqlBoundaries, Statement.RETURN_GENERATED_KEYS);
            stmtBoundaries.setString(1, boundary.getExternalId());
            stmtBoundaries.setString(2, boundary.getBoundaryName());
            stmtBoundaries.setDouble(3, boundary.getMinLatitude());
            stmtBoundaries.setDouble(4, boundary.getMaxLatitude());
            stmtBoundaries.setDouble(5, boundary.getMinLongitude());
            stmtBoundaries.setDouble(6, boundary.getMaxLongitude());
            stmtBoundaries.setString(7, boundary.getSimepleShapeUrl());
            stmtBoundaries.executeUpdate();
            logKeeper.appendLog("Inserted boundary external id: " + boundary.getExternalId());

            Helpers.sleep(1);

            return isInserted;
        } catch (SQLException e) {
            logKeeper.appendLog(e.getMessage());
        } finally {
            if (stmtBoundaries != null) {
                try {
                    stmtBoundaries.close();
                } catch (SQLException e) {
                    logKeeper.appendLog(e.getMessage());
                }
            }
        }

        return isInserted;
    }

    public List<Boundary> getHocBoundaries() {
        logKeeper.appendLog("Reading HOC members boundaries from DB");
        List<Boundary> boundaries = new ArrayList<>();
        String sql = "SELECT external_id, boundary_name, min_latitude, max_latitude, min_longitude, max_longitude, simple_shape_url FROM boundaries";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try (Connection conn = DbManager.getConn()) {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String externalId = rs.getString("external_id");
                String boundaryName = rs.getString("boundary_name");
                double minLatitude = rs.getDouble("min_latitude");
                double maxLatitude = rs.getDouble("max_latitude");
                double minLongitude = rs.getDouble("min_longitude");
                double maxLongitude = rs.getDouble("max_longitude");
                String simpleShapeUrl = rs.getString("simple_shape_url");
                Boundary boundary = new Boundary(
                        externalId,
                        boundaryName,
                        minLatitude,
                        maxLatitude,
                        minLongitude,
                        maxLongitude,
                        simpleShapeUrl
                );

                boundaries.add(boundary);
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

        return boundaries;
    }
}
