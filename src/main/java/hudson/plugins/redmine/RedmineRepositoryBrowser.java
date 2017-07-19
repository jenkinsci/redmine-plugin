package hudson.plugins.redmine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import hudson.scm.SubversionChangeLogSet;
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
 */
public class RedmineRepositoryBrowser extends SubversionRepositoryBrowser {
	private final String repositoryId; 

	@DataBoundConstructor
	public RedmineRepositoryBrowser(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	/**
	 * @deprecated use {@link #RedmineRepositoryBrowser(String)}
	 */
	@Deprecated
	public RedmineRepositoryBrowser() {
		this(null);
	}
	
	public String getRepositoryId() {
		return repositoryId;
	}

	@Override
	public URL getDiffLink(Path path) throws IOException {
		if(path.getEditType()!= EditType.EDIT) {
			return null;    
		}
		String filePath = getFilePath(path.getLogEntry(), path.getValue());
		int revision = path.getLogEntry().getRevision();
		
		if (isVersionBefore090(path.getLogEntry())) {
			URL baseUrl = getRedmineURL(path.getLogEntry());
			String projectName = getProject(path.getLogEntry());
			        
			return new URL(baseUrl, "repositories/diff/" + projectName + filePath + "?rev=" + revision);
		} else {
			URL baseUrl = getRedmineProjectURL(path.getLogEntry());
			String id = getRepositoryId(path.getLogEntry());

			return new URL(baseUrl, "repository" + id + "/diff" + filePath + "?rev=" + revision);
		}
	}

	@Override
	public URL getFileLink(Path path) throws IOException {
		String filePath = getFilePath(path.getLogEntry(), path.getValue());
		
		if (isVersionBefore090(path.getLogEntry())) {
			URL baseUrl = getRedmineURL(path.getLogEntry());
			String projectName = getProject(path.getLogEntry());
			        
			return baseUrl == null ? null : new URL(baseUrl, "repositories/entry/" + projectName + filePath);
		} else {
			URL baseUrl = getRedmineProjectURL(path.getLogEntry());
			String id = getRepositoryId(path.getLogEntry());
			int revision = path.getLogEntry().getRevision();

			return baseUrl == null ? null : new URL(baseUrl, "repository"+ id + "/revisions/"+ revision+"/entry" +filePath);
		}
	}

	@Override
	public URL getChangeSetLink(LogEntry changeSet) throws IOException {
		if (isVersionBefore090(changeSet)) {
			URL baseUrl = getRedmineURL(changeSet);
			String projectName = getProject(changeSet);
			return baseUrl == null ? null : new URL(baseUrl, "repositories/revision/" + projectName + "/" + changeSet.getRevision());
		} else {
			URL baseUrl = getRedmineProjectURL(changeSet);
			String id = getRepositoryId(changeSet);
			return baseUrl == null ? null : new URL(baseUrl, "repository" + id + "/revisions/" + changeSet.getRevision());
		}
	}

	@Override
	public Descriptor<RepositoryBrowser<?>> getDescriptor() {
		return DESCRIPTOR;
	}

	private URL getRedmineURL(LogEntry logEntry) throws MalformedURLException {
		AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
		RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
		if(rpp == null) {
			return null;
		} else {
			return new URL(rpp.getRedmineWebsite().baseUrl);
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
	
	/**
	* The raw Redmine URL is documented with the example http://myhost/redmine. This
	* function returns the _project_'s base URL which concatenates the redmine base url
	* with the "/projects/<projectName>/" (if a project name is supplied)
	* @param logEntry
	* @return
	* @throws MalformedURLException
	*/
	private URL getRedmineProjectURL(LogEntry logEntry) throws MalformedURLException {
		AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
		RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
		String url;
		if(rpp == null || rpp.getRedmineWebsite() == null) {
			url = "";
		} else {
			// NOTE: we force the website string to have a trailing slash in the constructor
			url = rpp.getRedmineWebsite().baseUrl;
			if (rpp.projectName != null) {
				url += "projects/" + rpp.projectName + "/";
			}
		}
		return new URL(url);
	}	

	private String getRepositoryId(LogEntry logEntry) {
		if (this.repositoryId == null || this.repositoryId.trim().length() == 0){
			return "";
		} else {
			return "/" + this.repositoryId.trim();
		}
	}

	private String getFilePath(LogEntry logEntry, String fileFullPath) {
		AbstractProject<?,?> p = (AbstractProject<?,?>)logEntry.getParent().build.getProject();
		RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);

		String filePath = "";
		if(VersionUtil.isVersionBefore081(rpp.getRedmineWebsite().versionNumber)) {
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
	
	private boolean isVersionBefore090(LogEntry logEntry) {
		SubversionChangeLogSet parent = logEntry.getParent();
		if (parent == null || parent.build == null) {
			return false;
		}
		AbstractProject<?, ?> p = parent.build.getProject();
		RedmineProjectProperty rpp = p.getProperty(RedmineProjectProperty.class);
		return rpp != null && VersionUtil.isVersionBefore090(rpp.getRedmineWebsite().versionNumber);
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
