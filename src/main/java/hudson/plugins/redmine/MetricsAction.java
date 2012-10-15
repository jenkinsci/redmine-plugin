package hudson.plugins.redmine;

import hudson.model.Action;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.ArrayList;
import java.util.List;

public class MetricsAction implements Action {

	private AbstractBuild<?, ?> build;
	private List<MetricsResult> metricsList;

	public MetricsAction(AbstractBuild<?, ?> build,
			List<MetricsResult> metricsList) {
		this.build = build;
		this.metricsList = metricsList;
	}

	public String getIconFileName() {
		return "notepad.png";
	}

	public String getDisplayName() {
		return Messages.ticket_metrics_detail();
	}

	public String getUrlName() {
		return "ticketMetrics";
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public List<MetricsResult> getMetricsList() {
		return metricsList;
	}

	public List<List<MetricsResult>> getPreviousMetricsLists() {
		@SuppressWarnings("unchecked")
		List<AbstractBuild<?, ?>> builds = (List<AbstractBuild<?, ?>>) build
				.getPreviousBuildsOverThreshold(1000, Result.SUCCESS);
		List<List<MetricsResult>> results = new ArrayList<List<MetricsResult>>();
		for (AbstractBuild<?, ?> abstractBuild : builds) {
			MetricsAction action = abstractBuild.getAction(MetricsAction.class);
			if (action == null) {
				continue;
			}
			results.add(action.getMetricsList());
		}
		return results;
	}
}
