package hudson.plugins.redmine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.kohsuke.stapler.DataBoundConstructor;

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

	@DataBoundConstructor
    public RedmineRepositoryBrowser() {
    }
	
	@Override
	public URL getDiffLink(Path path) throws IOException {
		if(path.getEditType()!= EditType.EDIT) {
            return null;    
		}
        URL baseUrl = getRedmineURL(path.getLogEntry());
        String projectName = getProject(path.getLogEntry());
        String filePath = getFilePath(path.getValue());
        
        int revision = path.getLogEntry().getRevision();
        return new URL(baseUrl, "repositories/diff/" + projectName + filePath + "?rev=" + revision);
	}

	@Override
	public URL getFileLink(Path path) throws IOException {
		URL baseUrl = getRedmineURL(path.getLogEntry());
		String projectName = getProject(path.getLogEntry());
		String filePath = getFilePath(path.getValue());
        
        return baseUrl == null ? null : new URL(baseUrl, "repositories/entry/" + projectName + filePath);
	}

	@Override
	public URL getChangeSetLink(LogEntry changeSet) throws IOException {
		URL baseUrl = getRedmineURL(changeSet);
		String projectName = getProject(changeSet);
        return baseUrl == null ? null : new URL(baseUrl, "repositories/revision/" + projectName + "/" + changeSet.getRevision());
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

	private String getProject(LogEntry logEntry) {
		AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
		RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
        if(rpp == null) {
        	return null;
        } else {
        	return rpp.projectName;
        }
	}
	
	private String getFilePath(String fileFullPath) {
		String[] filePaths = fileFullPath.split("/");
        String filePath = "/";
        if(filePaths.length > 2) {
        	for(int i = 2 ; i < filePaths.length; i++) {
        		filePath = filePath + filePaths[i];
        		if(i != filePaths.length - 1) {
        			filePath = filePath + "/";
        		}
        	}
        }
        return filePath;
        
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
