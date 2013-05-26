package hudson.plugins.redmine;

import java.util.List;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.CopyOnWriteList;
import hudson.util.FormValidation;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Property for {@link AbstractProject} that stores the associated Redmine website URL.
 *
 * @author gaooh
 * @date 2008/10/13
 */
public class RedmineProjectProperty extends JobProperty<AbstractProject<?, ?>> {
	public final RedmineWebsiteConfig redmineWebsite;
	public final String projectName;
	
	@DataBoundConstructor
	public RedmineProjectProperty(String redmineWebsite, String projectName) {
		RedmineWebsiteConfig foundRedmine = null;
		for (RedmineWebsiteConfig redmineConfig :  DESCRIPTOR.getRedmineWebsites()) {
			if (redmineConfig.baseUrl.equals(redmineWebsite)){
				foundRedmine = redmineConfig;
				break;
			}
		}
		
		this.redmineWebsite = foundRedmine;
		this.projectName = projectName;
	}

	@Override
    public Action getJobAction(AbstractProject<?,?> job) {
		return new RedmineLinkAction(redmineWebsite, projectName);
    }

	@Override
	public JobPropertyDescriptor getDescriptor() {
		return DESCRIPTOR;
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	public static final class DescriptorImpl extends JobPropertyDescriptor {
		private final CopyOnWriteList<RedmineWebsiteConfig> redmineWebsites = new CopyOnWriteList<RedmineWebsiteConfig>();

		public DescriptorImpl() {
			super(RedmineProjectProperty.class);
			load();
		}

		@Override
		public boolean isApplicable(Class<? extends Job> jobType) {
			return AbstractProject.class.isAssignableFrom(jobType);
		}

		public String getDisplayName() {
			return "Associated Redmine website";
		}
		
		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			this.redmineWebsites.replaceBy(req.bindJSONToList(RedmineWebsiteConfig.class, 
					formData.get("redmineWebsites")));
			
			save();
			return super.configure(req, formData);
		}

		@Override
		public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			if (formData.containsKey("redmine")){
				JSONObject redmineJson = formData.getJSONObject("redmine");
				if (!StringUtils.isBlank(redmineJson.optString("redmineWebsite")) 
						&& !StringUtils.isBlank(redmineJson.optString("projectName"))) {
					return req.bindJSON(RedmineProjectProperty.class, redmineJson);
				}
			}
			
			return null;
		}
		
		public List<RedmineWebsiteConfig> getRedmineWebsites() {
			return this.redmineWebsites.getView();
		}
       
		public FormValidation doCheckProjectName(@QueryParameter String projectName) {
			if (projectName == null || projectName.trim().length() < 1) {
				return FormValidation.error("Project name can't be empty!");
			}
			   
			//We dont validate existence yet
			return FormValidation.ok();
		}

	}
}
