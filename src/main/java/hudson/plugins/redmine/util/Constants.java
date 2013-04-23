package hudson.plugins.redmine.util;

/**
 * @author Yasuyuki Saito
 */
public abstract class Constants {

    private Constants() {}

    /** Default DB Server */
    public static final String DEFAULT_DB_SERVER = "127.0.0.1";

    /** Default DatabaseName */
    public static final String DEFAULT_DATABASE_NAME = "redmine";

    /** Redmine Version 1.2.0 */
    public static final String VERSION_1_2_0  = "1.2.0";

    /** Redmine Version 1.1.3 */
    public static final String VERSION_1_1_3  = "1.1.3";

    /** Redmine Default Login Table */
    public static final String DEFAULT_LOGIN_TABLE = "users";

    /** Redmine Default User Field */
    public static final String DEFAULT_USER_FIELD = "login";

    /** Redmine Default Password Field */
    public static final String DEFAULT_PASSWORD_FIELD = "hashed_password";

    /** Redmine Default Salt Field */
    public static final String DEFAULT_SALT_FIELD = "salt";


    /** Redmine DBMS: MySQL */
    public static final String DBMS_MYSQL = "MySQL";

    /** Redmine DBMS: PostgreSQL */
    public static final String DBMS_POSTGRESQL = "PostgreSQL";


    /** Connection String Format: MySQL */
    public static final String CONNECTION_STRING_FORMAT_MYSQL = "jdbc:mysql://%s:%s/%s";

    /** Default Port: MySQL */
    public static final String DEFAULT_PORT_MYSQL = "3306";

    /** JDBC Driver Name: MySQL */
    public static final String JDBC_DRIVER_NAME_MYSQL = "com.mysql.jdbc.Driver";


    /** Connection String Format: PostgreSQL */
    public static final String CONNECTION_STRING_FORMAT_POSTGRESQL = "jdbc:postgresql://%s:%s/%s";

    /** Default Port: PostgreSQL */
    public static final String DEFAULT_PORT_POSTGRESQL = "5432";

    /** JDBC Driver Name: PostgreSQL */
    public static final String JDBC_DRIVER_NAME_POSTGRESQL = "org.postgresql.Driver";

}
