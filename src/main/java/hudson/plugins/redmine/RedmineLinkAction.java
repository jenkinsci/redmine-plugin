package hudson.plugins.redmine;

import hudson.model.Action;

/**
 * @author gaooh
 * @date 2008/10/26
 */
public class RedmineLinkAction implements Action {
	
	private final RedmineProjectProperty prop;

    public RedmineLinkAction(RedmineProjectProperty prop) {
        this.prop = prop;
    }

    public String getIconFileName() {
        return "/plugin/redmine/ruby-logo-R.png"; // quick-fix. if created the official logo, it is use.
    }

    public String getDisplayName() {
        return "Redmine";
    }

    public String getUrlName() {
        return prop.redmineWebsite;
    }
    
}
