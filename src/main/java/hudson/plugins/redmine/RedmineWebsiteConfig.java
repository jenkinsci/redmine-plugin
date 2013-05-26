package hudson.plugins.redmine;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * @since 0.14
 * @author ljader
 *
 */
public class RedmineWebsiteConfig extends AbstractDescribableImpl<RedmineWebsiteConfig> {
	public String baseUrl;
	public String versionNumber;
	
	/**
	 * Constructor; params shouldnt be null
	 * 
	 * @param baseUrl
	 * @param versionNumber
	 */
	@DataBoundConstructor
	public RedmineWebsiteConfig(String baseUrl, String versionNumber) {
		this.baseUrl = baseUrl;
		if (!this.baseUrl.endsWith("/")) {
			this.baseUrl += '/';
		}
		this.versionNumber = versionNumber;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedmineWebsiteConfig that = (RedmineWebsiteConfig) o;

        if (baseUrl != null ? !baseUrl.equals(that.baseUrl) : that.baseUrl != null) 
        	return false;

        return true;
    }
	
    @Override
    public int hashCode() {
        int result;
        result = (baseUrl != null ? baseUrl.hashCode() : 0);
        return result;
    }
    
    @Extension
    public static class DescriptorImpl extends Descriptor<RedmineWebsiteConfig> {
        @Override
        public String getDisplayName() {
            return "";
        }
        
        public FormValidation doCheckBaseUrl(@QueryParameter String baseUrl) {
    		if (baseUrl == null || baseUrl.trim().length() < 1) {
    			return FormValidation.error("Url can't be empty!");
    		}
    		   
    		return FormValidation.ok();
    	}

        public FormValidation doCheckVersionNumber(@QueryParameter String versionNumber) {
        	if (versionNumber == null || versionNumber.trim().length() < 1) {
        		return FormValidation.error("Version number can't be empty!");
        	}
        	
        	return FormValidation.ok();
        }
    }
}