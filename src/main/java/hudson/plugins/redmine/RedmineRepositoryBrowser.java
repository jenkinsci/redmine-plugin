package hudson.plugins.redmine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
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
		String filePath = getFilePath(path.getLogEntry(), path.getValue());

		int revision = path.getLogEntry().getRevision();
		return new URL(baseUrl, "repositories/diff/" + projectName + filePath + "?rev=" + revision);
	}

	@Override
	public URL getFileLink(Path path) throws IOException {
		URL baseUrl = getRedmineURL(path.getLogEntry());
		String repo = getProject(path.getLogEntry());
		String filePath = getFilePath(path.getLogEntry(), path.getValue());
		int revision = path.getLogEntry().getRevision();

		return baseUrl == null ? null : new URL(baseUrl, "repository"+ repo + "/revisions/"+ revision+"/entry" +filePath);
	}

	@Override
	public URL getChangeSetLink(LogEntry changeSet) throws IOException {
		URL baseUrl = getRedmineURL(changeSet);
		String repo = getProject(changeSet);
		return baseUrl == null ? null : new URL(baseUrl, "repository" +repo+ "/revisions/" + changeSet.getRevision());
	}

	@Override
	public Descriptor<RepositoryBrowser<?>> getDescriptor() {
		return DESCRIPTOR;
	}

	/**
	 * The raw Redmine URL is documented with the example http://myhost/redmine.  This 
	 * function returns the _project_'s base URL which concatenates the redmine base url 
	 * with the "/projects/<projectName>/" (if a project name is supplied)
	 * @param logEntry
	 * @return
	 * @throws MalformedURLException
	 */
	private URL getRedmineURL(LogEntry logEntry) throws MalformedURLException {
		AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
		RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
		String url;
		if(rpp == null) {
			url = "";
		} else {
			// NOTE: we force the website string to have a trailing slash in the constructor
			url = rpp.redmineWebsite;
			if (rpp.projectName != null) {
				url += "projects/" + rpp.projectName + "/";
			}
		}
		return new URL(url);
	}

	private String getProject(LogEntry logEntry) {
		AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
		RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
		if(rpp == null) {
			return "";
		} else if (rpp.projectRepoName == null){
			return "";
		} else {
			return "/" + rpp.projectRepoName;
		}
	}
	
	private String getFilePath(LogEntry logEntry, String fileFullPath) {
		AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
		RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);

		String filePath = "";
		if(VersionUtil.isVersionBefore081(rpp.redmineVersionNumber)) {
			String[] filePaths = fileFullPath.split("/");
			filePath = "/";
			if(filePaths.length > 2) {
				for(int i = 2 ; i < filePaths.length; i++) {
					filePath = filePath + filePaths[i];
					if(i != filePaths.length - 1) {
						filePath = filePath + "/";
					}
				}
			}
		} else { 
        		filePath = fileFullPath;
		}
		return filePath;

	}
	
	@Extension
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
