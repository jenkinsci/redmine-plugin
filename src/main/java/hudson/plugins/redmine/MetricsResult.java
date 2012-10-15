package hudson.plugins.redmine;

public class MetricsResult {
	private String status;
	private int count;

	public MetricsResult(String status, int count) {
		this.status = status;
		this.count = count;
	}

	public String getStatus() {
		return status;
	}

	public int getCount() {
		return count;
	}

}
