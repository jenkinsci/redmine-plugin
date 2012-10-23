package hudson.plugins.redmine;

import com.taskadapter.redmineapi.RedmineException;

public class MetricsException extends Exception {

	private static final long serialVersionUID = 7335586079552372270L;

	public MetricsException(RedmineException e) {
		super(e);
	}

}
