package hudson.plugins.redmine;

import hudson.model.Action;

/**
 * @author gaooh
 * @date 2008/10/26
 */
public class RedmineLinkAction implements Action {
	private final RedmineWebsiteConfig redmineWebsite;
	private final String projectName;

    public RedmineLinkAction(RedmineWebsiteConfig redmineWebsite, String projectName) {
		this.redmineWebsite = redmineWebsite;
		this.projectName = projectName;
	}

    public String getIconFileName() {
        return "/plugin/redmine/redmine-logo.png"; // redmine logo instead ruby
    }

    public String getDisplayName() {
        return "Redmine - " + projectName;
    }

    public String getUrlName() {
        return redmineWebsite.baseUrl + "projects/" + projectName;
    }
    
}
