package hudson.plugins.redmine;

import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.util.Graph;

public class MetricsProjectAction implements Action {

	private AbstractProject<?, ?> project;

	public MetricsProjectAction(AbstractProject<?, ?> project) {
		this.project = project;
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getDisplayName() {
		return Messages.ticket_metrics();
	}

	public String getUrlName() {
		return "metricsProject";
	}

	public Graph getGraph() {
		return new MetricsGraph(project);
	}
}
