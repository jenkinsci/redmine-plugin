package hudson.plugins.redmine;

import hudson.Plugin;
import hudson.model.Jobs;
import hudson.scm.RepositoryBrowsers;

/**
 * Entry point of for the Redmine plugin.
 * 
 * @plugin
 * @author gaooh
 * @date 2008/10/26
 */
public class PluginImpl extends Plugin {
	private final RedmineLinkAnnotator annotator = new RedmineLinkAnnotator();

    @Override
    public void start() throws Exception {
        annotator.register();
        Jobs.PROPERTIES.add(RedmineProjectProperty.DESCRIPTOR);
        RepositoryBrowsers.LIST.add(RedmineRepositoryBrowser.DESCRIPTOR);
    }

    public void stop() throws Exception {
        annotator.unregister();
    }
    
}
