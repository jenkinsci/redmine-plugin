package hudson.plugins.redmine;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.redmine.dao.*;
import hudson.plugins.redmine.util.CipherUtil;
import hudson.plugins.redmine.util.Constants;
import hudson.security.AbstractPasswordBasedSecurityRealm;
import hudson.security.GroupDetails;
import hudson.security.SecurityRealm;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.springframework.dao.DataAccessException;

/**
 * @author Yasuyuki Saito
 */
public class RedmineSecurityRealm extends AbstractPasswordBasedSecurityRealm {

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(RedmineSecurityRealm.class.getName());

    /** Redmine DBMS */
    private final String dbms;

    /** DB Server */
    private final String dbServer;

    /** Database Name */
    private final String databaseName;

    /** Database Port */
    private final String port;

    /** Database UserName */
    private final String dbUserName;

    /** Database Password */
    private final String dbPassword;

    /** Redmine Version */
    private final String version;

    /** Redmine Login Table */
    private final String loginTable;

    /** Redmine User Field */
    private final String userField;

    /** Redmine Password Field */
    private final String passField;

    /** Redmine Salt Field */
    private final String saltField;

    /**
     * Constructor
     * @param dbms         Redmine DBMS
     * @param dbServer     DB Server
     * @param databaseName Database Name
     * @param port         Database Port
     * @param dbUserName   Database UserName
     * @param dbPassword   Database Password
     * @param version      Redmine Version
     * @param loginTable   Redmine Login Table
     * @param userField    Redmine User Field
     * @param passField    Redmine Password Field
     * @param saltField    Redmine Salt Field
     */
    @DataBoundConstructor
    public RedmineSecurityRealm(String dbms, String dbServer, String databaseName, String port, String dbUserName, String dbPassword,
            String version, String loginTable, String userField, String passField, String saltField) {

        this.dbms         = StringUtils.isBlank(dbms)         ? Constants.DBMS_MYSQL              : dbms;
        this.dbServer     = StringUtils.isBlank(dbServer)     ? Constants.DEFAULT_DB_SERVER       : dbServer;
        this.databaseName = StringUtils.isBlank(databaseName) ? Constants.DEFAULT_DATABASE_NAME   : databaseName;

        if (StringUtils.isBlank(port))
            this.port = (Constants.DBMS_MYSQL.equals(this.dbms)) ? (Constants.DEFAULT_PORT_MYSQL) : (Constants.DBMS_POSTGRESQL);
        else
            this.port = port;

        this.dbUserName   = dbUserName;
        this.dbPassword   = dbPassword;
        this.version      = StringUtils.isBlank(version)      ? Constants.VERSION_1_2_0           : version;

        this.loginTable   = StringUtils.isBlank(loginTable)   ? Constants.DEFAULT_LOGIN_TABLE     : loginTable;
        this.userField    = StringUtils.isBlank(userField)    ? Constants.DEFAULT_USER_FIELD      : userField;
        this.passField    = StringUtils.isBlank(passField)    ? Constants.DEFAULT_PASSWORD_FIELD  : passField;
        this.saltField    = StringUtils.isBlank(saltField)    ? Constants.DEFAULT_SALT_FIELD      : saltField;
    }


    public static final class DescriptorImpl extends Descriptor<SecurityRealm> {
        @Override
        public String getHelpFile() {
            return "/plugin/redmine/help-auth-overview.html";
        }
        @Override
        public String getDisplayName() {
            return Messages.RedmineSecurityRealm_DisplayName();
        }
    }


    @Extension
    public static DescriptorImpl install() {
        return new DescriptorImpl();
    }

    /**
     *
     * @author Yasuyuki Saito
     */
    class Authenticator extends AbstractUserDetailsAuthenticationProvider {
        @Override
        protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        }

        @Override
        protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
            return RedmineSecurityRealm.this.authenticate(username, authentication.getCredentials().toString());
        }
    }

    /**
     *
     * @param username Login UserName
     * @param password Login Password
     */
    @Override
    protected UserDetails authenticate(String username, String password) throws AuthenticationException {
        AbstractAuthDao dao = null;

        try {
            dao = createAuthDao(this.dbms);

            LOGGER.info("Redmine DBMS      : " + this.dbms);
            LOGGER.info("DB Server         : " + this.dbServer);
            LOGGER.info("DB Port           : " + this.port);
            LOGGER.info("Database Name     : " + this.databaseName);

            dao.open(this.dbServer, this.port, this.databaseName, this.dbUserName, this.dbPassword);

            if (!dao.isTable(this.loginTable))
                throw new RedmineAuthenticationException("RedmineSecurity: Invalid Login Table");

            if (!dao.isField(this.loginTable, this.userField))
                throw new RedmineAuthenticationException("RedmineSecurity: Invalid User Field");

            RedmineUserData userData = dao.getRedmineUserData(this.loginTable, this.userField, this.passField, Constants.VERSION_1_2_0.equals(this.version) ? this.saltField : null, username);

            if (userData == null) {
                LOGGER.warning("RedmineSecurity: Invalid Username");
                throw new UsernameNotFoundException("RedmineSecurity: User not found");
            }

            String encryptedPassword = "";
            if (Constants.VERSION_1_2_0.equals(this.version)) {
                encryptedPassword = CipherUtil.encodeSHA1(userData.getSalt() + CipherUtil.encodeSHA1(password));
            } else if (Constants.VERSION_1_1_3.equals(this.version)) {
                encryptedPassword =  CipherUtil.encodeSHA1(password);
            }

            LOGGER.info("Redmine Version   : " + this.version);
            LOGGER.info("User Name         : " + username);
            LOGGER.info("Encrypted Password: " + encryptedPassword);

            if (!userData.getPassword().equals(encryptedPassword)) {
                LOGGER.warning("RedmineSecurity: Invalid Password");
                throw new RedmineAuthenticationException("RedmineSecurity: Invalid Password");
            }

            return getUserDetails(username, userData.getPassword());
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: System.Exception", e);
        } finally {
            if (dao != null) dao.close();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        AbstractAuthDao dao = null;

        try {
            dao = createAuthDao(this.dbms);

            dao.open(this.dbServer, this.port, this.databaseName, this.dbUserName, this.dbPassword);

            if (!dao.isTable(this.loginTable))
                throw new RedmineAuthenticationException("RedmineSecurity: Invalid Login Table");

            if (!dao.isField(this.loginTable, this.userField))
                throw new RedmineAuthenticationException("RedmineSecurity: Invalid User Field");

            RedmineUserData userData = dao.getRedmineUserData(this.loginTable, this.userField, this.passField, Constants.VERSION_1_2_0.equals(this.version) ? this.saltField : null, username);

            if (userData == null) {
                LOGGER.warning("RedmineSecurity: Invalid Username");
                throw new UsernameNotFoundException("RedmineSecurity: User not found");
            }

            return getUserDetails(username, userData.getPassword());
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: System.Exception", e);
        } finally {
            if (dao != null) dao.close();
        }
    }

    /**
     * Create Auth Dao
     * @param  dbms
     * @return
     */
    private AbstractAuthDao createAuthDao(String dbms) {
        if (Constants.DBMS_MYSQL.equals(dbms))
            return new MySQLAuthDao();
        else if (Constants.DBMS_POSTGRESQL.equals(dbms))
            return new PostgreSQLAuthDao();
        else
            return null;
    }

    @Override
    public GroupDetails loadGroupByGroupname(String groupname) throws UsernameNotFoundException, DataAccessException {
        throw new UsernameNotFoundException("RedmineSecurityRealm: Non-supported function");
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    private UserDetails getUserDetails(String username, String password) {
        Set<GrantedAuthority> groups = new HashSet<GrantedAuthority>();
        groups.add(SecurityRealm.AUTHENTICATED_AUTHORITY);
        return new RedmineUserDetails(username, password, true, true, true, true, groups.toArray(new GrantedAuthority[groups.size()]));
    }


    /**
     *
     * @return
     */
    public String getDbms() {
        return dbms;
    }

    /**
     *
     * @return
     */
    public String getDbServer() {
        return dbServer;
    }

    /**
     *
     * @return
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     *
     * @return
     */
    public String getPort() {
        return port;
    }

    /**
     *
     * @return
     */
    public String getDbUserName() {
        return dbUserName;
    }

    /**
     *
     * @return
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @return
     */
    public String getLoginTable() {
        return loginTable;
    }

    /**
     *
     * @return
     */
    public String getUserField() {
        return userField;
    }

    /**
     *
     * @return
     */
    public String getPassField() {
        return passField;
    }

    /**
     *
     * @return
     */
    public String getSaltField() {
        return saltField;
    }
}
