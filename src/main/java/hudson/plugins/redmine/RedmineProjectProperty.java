package hudson.plugins.redmine;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Property for {@link AbstractProject} that stores the associated Redmine website URL.
 * 
 * @author gaooh
 * @date 2008/10/13
 */
public class RedmineProjectProperty extends JobProperty<AbstractProject<?, ?>> {

	public final String redmineWebsite;

	@DataBoundConstructor
	public RedmineProjectProperty(String redmineWebsite) {
		if (StringUtils.isBlank(redmineWebsite)) {
			redmineWebsite = null;
		} else {
			if (!redmineWebsite.endsWith("/")) {
				redmineWebsite += '/';
			}
		}
		this.redmineWebsite = redmineWebsite;
	}

	@Override
    public Action getJobAction(AbstractProject<?,?> job) {
        return new RedmineLinkAction(this);
    }
	
	@Override
	public JobPropertyDescriptor getDescriptor() {
		return DESCRIPTOR;
	}

	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	public static final class DescriptorImpl extends JobPropertyDescriptor {
		private transient String redmineWebsite;

		public DescriptorImpl() {
			super(RedmineProjectProperty.class);
			load();
		}

		public boolean isApplicable(Class<? extends Job> jobType) {
			return AbstractProject.class.isAssignableFrom(jobType);
		}

		public String getDisplayName() {
			return "Associated Redmine website";
		}

		@Override
		public JobProperty<?> newInstance(StaplerRequest req) throws FormException {
			try {
				return new RedmineProjectProperty(req.getParameter("redmine.redmineWebsite"));
			} catch (IllegalArgumentException e) {
				throw new FormException("redmine.redmineWebsite", "redmine.redmineWebSite");
			}
		}
		
	}
}
