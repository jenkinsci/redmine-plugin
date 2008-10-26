package hudson.plugins.redmine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SubversionRepositoryBrowser;
import hudson.scm.SubversionChangeLogSet.LogEntry;
import hudson.scm.SubversionChangeLogSet.Path;

/**
 * produces redmine links.
 * 
 * @author gaooh
 * @date 2008/10/26
 */
public class RedmineRepositoryBrowser extends SubversionRepositoryBrowser {

	@Override
	public URL getDiffLink(Path path) throws IOException {
		if(path.getEditType()!= EditType.EDIT) {
            return null;    
		}
        URL baseUrl = getRedmineURL(path.getLogEntry());
        int revision = path.getLogEntry().getRevision();
        return new URL(baseUrl, "repositories/diff/" + path.getValue() + "?rev=" + revision);
	}

	@Override
	public URL getFileLink(Path path) throws IOException {
		URL baseUrl = getRedmineURL(path.getLogEntry());
        return baseUrl == null ? null : new URL(baseUrl, "repositories/browse" + path.getValue());
	}

	@Override
	public URL getChangeSetLink(LogEntry changeSet) throws IOException {
		URL baseUrl = getRedmineURL(changeSet);
        return baseUrl == null ? null : new URL(baseUrl, "repositories/revision/" + changeSet.getRevision());
	}

	public Descriptor<RepositoryBrowser<?>> getDescriptor() {
		 return DESCRIPTOR;
	}
	
	private URL getRedmineURL(LogEntry logEntry) throws MalformedURLException {
        AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
        RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
        if(rpp == null) {
        	return null;
        } else {
        	return new URL(rpp.redmineWebsite);
        }
    }

	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<RepositoryBrowser<?>> {
        public DescriptorImpl() {
            super(RedmineRepositoryBrowser.class);
        }

        public String getDisplayName() {
            return "Redmine";
        }
    }
}
