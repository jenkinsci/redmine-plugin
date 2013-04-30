package hudson.plugins.redmine.dao;

import hudson.plugins.redmine.RedmineAuthenticationException;
import hudson.plugins.redmine.RedmineUserData;

import java.sql.Connection;

/**
 * @author Yasuyuki Saito
 */
public abstract class AbstractAuthDao {

    /** DB Connection */
    protected Connection conn = null;

    /**
     * DB Connection Open.
     * @param dbServer     DB Server
     * @param port         Database Port
     * @param databaseName Database Name
     * @param dbUserName   Database UserName
     * @param dbPassword   Database Password
     * @throws RedmineAuthenticationException
     */
    public abstract void open(String dbServer, String port, String databaseName, String dbUserName, String dbPassword)
            throws RedmineAuthenticationException;

    /**
     * DB Conncetion Close.
     */
    public void close() {
        if (conn != null) {
            try { conn.close(); } catch (Exception e) {}
        }
    }

    /**
     * Table Check.
     * @param  table
     * @return
     * @throws RedmineAuthenticationException
     */
    public abstract boolean isTable(String table) throws RedmineAuthenticationException;

    /**
     * Field Check.
     * @param  table
     * @param  field
     * @return
     * @throws RedmineAuthenticationException
     */
    public abstract boolean isField(String table, String field) throws RedmineAuthenticationException;

    /**
     * Get RedmineUserData.
     * @param  loginTable
     * @param  userField
     * @param  passField
     * @param  saltField
     * @param  username
     * @return
     * @throws RedmineAuthenticationException
     */
    public abstract RedmineUserData getRedmineUserData(String loginTable, String userField, String passField, String saltField, String username) throws RedmineAuthenticationException;

}
