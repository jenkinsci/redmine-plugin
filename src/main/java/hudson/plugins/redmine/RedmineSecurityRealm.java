package hudson.plugins.redmine;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.security.AbstractPasswordBasedSecurityRealm;
import hudson.security.GroupDetails;
import hudson.security.SecurityRealm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.kohsuke.stapler.DataBoundConstructor;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.User;

/**
 * @author Yasuyuki Saito
 */
public class RedmineSecurityRealm extends AbstractPasswordBasedSecurityRealm {

    /** Redmine Web Site */
    private final String webSite;

    /** API Key */
    private final String apiKey;

    /**
     * Constructor
     * @param webSite Redmine Web Site
     * @param apiKey  API Key
     */
    @DataBoundConstructor
    public RedmineSecurityRealm(String webSite, String apiKey) {
        this.webSite = webSite;
        this.apiKey  = apiKey;
    }


    public static final class DescriptorImpl extends Descriptor<SecurityRealm> {
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
        RedmineManager manager = null;
        try {
            manager = new RedmineManager(webSite, username, password);

            List<User> users = manager.getUsers();

            if (users == null)
                throw new UsernameNotFoundException("RedmineSecurity: User not found");

            return getUserDetails(username, password);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: System.Exception", e);
        } finally {
            if (manager != null) manager.shutdown();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RedmineManager manager = null;
        try {
            manager = new RedmineManager(webSite, apiKey);

            List<User> users = manager.getUsers();

            if (users == null) {
                throw new UsernameNotFoundException("RedmineSecurity: User not found");
            }

            for (User user : users) {
                if (user.getLogin().equals(username))
                    return getUserDetails(username, user.getPassword());
            }

            throw new UsernameNotFoundException("RedmineSecurity: User not found");
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: System.Exception", e);
        } finally {
            if (manager != null) manager.shutdown();
        }
    }


    @Override
    public GroupDetails loadGroupByGroupname(String groupname) throws UsernameNotFoundException {
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

    public String getWebSite() {
        return webSite;
    }

    public String getApiKey() {
        return apiKey;
    }
}
