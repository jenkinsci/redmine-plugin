package hudson.plugins.redmine;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class RedmineMetricsPublisher extends Publisher {

	private String url;
	private String projectName;
	private String apiKey;
	private String versions;
	private String ignoreTicketTracker;
	private String ignoreTicketStatus;

	@SuppressWarnings("deprecation")
	@DataBoundConstructor
	public RedmineMetricsPublisher(String url, String projectName,
			String apiKey, String versions, String ignoreTicketTracker,
			String ignoreTicketStatus) {
		this.url = url;
		this.projectName = projectName;
		this.apiKey = apiKey;
		this.versions = versions;
		this.ignoreTicketTracker = ignoreTicketTracker;
		this.ignoreTicketStatus = ignoreTicketStatus;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		PrintStream logger = listener.getLogger();

		RedmineMetricsCalculator calculator = new RedmineMetricsCalculator(url,
				apiKey, projectName, versions, ignoreTicketTracker,
				ignoreTicketStatus);
		try {
			List<MetricsResult> metricsList = calculator.calc();
			MetricsAction metricsAction = new MetricsAction(build, metricsList);
			build.addAction(metricsAction);
		} catch (MetricsException e) {
			logger.println(e);
			return false;
		}

		return true;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	public String getUrl() {
		return url;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getVersions() {
		return versions;
	}

	public String getIgnoreTicketTracker() {
		return ignoreTicketTracker;
	}

	public String getIgnoreTicketStatus() {
		return ignoreTicketStatus;
	}

	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return new MetricsProjectAction(project);
	}

	@Extension
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		@SuppressWarnings("rawtypes")
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		public FormValidation doCheckUrl(@QueryParameter String url) {
			if (url.length() == 0) {
				return FormValidation.error(Messages
						.error_require_redmine_url());
			}
			try {
				new URL(url);
			} catch (MalformedURLException e) {
				return FormValidation.error(Messages
						.error_invalid_redmine_url());
			}
			return FormValidation.ok();
		}

		public FormValidation doCheckApiKey(@QueryParameter String value) {
			if (value.length() == 0) {
				return FormValidation.error(Messages.error_require_api_key());
			}
			return FormValidation.ok();
		}

		public FormValidation doCheckProjectName(
				@QueryParameter String projectName) {
			if (projectName.length() == 0) {
				return FormValidation.error(Messages
						.error_require_project_name());
			}
			return FormValidation.ok();
		}

		@Override
		public String getDisplayName() {
			return Messages.aggregate_redmine_ticket_metrics();
		}

	}

}
