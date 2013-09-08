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
	public String name;
	public String baseUrl;
	public String versionNumber;
	
	/**
	 * Constructor; params shouldnt be null
	 * 
	 * @param baseUrl
	 * @param versionNumber
	 */
	@DataBoundConstructor
	public RedmineWebsiteConfig(String name, String baseUrl, String versionNumber) {
		this.name = name;
		this.baseUrl = baseUrl;
		if (!this.baseUrl.endsWith("/")) {
			this.baseUrl += '/';
		}
		this.versionNumber = versionNumber;
	}
	
    @Extension
    public static class DescriptorImpl extends Descriptor<RedmineWebsiteConfig> {
        @Override
        public String getDisplayName() {
            return "";
        }
        
        public FormValidation doCheckName(@QueryParameter String name) {
        	if (name == null || name.trim().length() < 1) {
        		return FormValidation.error("Name can't be empty!");
        	}
        	
        	return FormValidation.ok();
        }
        
        public FormValidation doCheckBaseUrl(@QueryParameter String baseUrl) {
    		if (baseUrl == null || baseUrl.trim().length() < 1) {
    			return FormValidation.error("Url can't be empty!");
    		}
    		   
    		return FormValidation.ok();
    	}

        public FormValidation doCheckVersionNumber(@QueryParameter String versionNumber) {
        	if (versionNumber == null || versionNumber.trim().length() < 1) {
        		return FormValidation.ok();
        	}
        	
        	if (versionNumber.split("\\.").length != 3) {
        		return FormValidation.error("Version number must be X.Y.Z form!");
        	}
        	
        	return FormValidation.ok();
        }
    }
}