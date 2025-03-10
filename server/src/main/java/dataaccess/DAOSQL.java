package dataaccess;

import java.sql.SQLException;

abstract class DAOSQL {
    protected String tableName;
    protected final String[] createStatement;

    DAOSQL (String tableName, String[] createStatement) throws DataAccessException {
        this.tableName = tableName;
        this.createStatement = createStatement.clone();
        configureDatabase();
    }

    protected String getTableName() {
        return tableName;
    }

    protected void configureDatabase () throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection())  {
            for (var statement : createStatement) {
                try(var prepatedStatement = conn.prepareStatement(statement)) {
                    prepatedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to init table " + tableName + ": " + e.getMessage());
        }
    }
}
