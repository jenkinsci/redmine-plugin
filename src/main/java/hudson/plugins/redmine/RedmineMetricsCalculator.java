package hudson.plugins.redmine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

public class RedmineMetricsCalculator {

  private String url;
  private String apiKey;
  /** actually a project identifier - project names in Redmine can have spaces */
  private String projectName;
  private String versions;
  private String ignoreTicketTracker;
  private String ignoreTicketStatus;

  public RedmineMetricsCalculator(String url, String apiKey,
      String projectName, String versions, String ignoreTicketTracker,
      String ignoreTicketStatus) {
    this.url = url;
    this.apiKey = apiKey;
    this.projectName = projectName;
    this.versions = versions;
    this.ignoreTicketTracker = ignoreTicketTracker;
    this.ignoreTicketStatus = ignoreTicketStatus;
  }

  public List<MetricsResult> calc() throws MetricsException {
    List<MetricsResult> result = new ArrayList<MetricsResult>();
    try {
      RedmineManager manager = new RedmineManager(url, apiKey);
      
      Project proj = getProject(manager);

      List<String> versionsList = getVersionsString(manager, proj);

      Map<String, Integer> tmpCalcMap = new HashMap<String, Integer>();
      for (String v : versionsList) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("project_id", proj.getId().toString());
        params.put("fixed_version_id", v);
        params.put("status_id", "*");

        for (Issue issue : manager.getIssues(params)) {
          if (!isTargetTracker(issue)) {
            continue;
          }
          if (!isTargetStatus(issue)) {
            continue;
          }

          String status = issue.getStatusName();
          if (!tmpCalcMap.containsKey(status)) {
            tmpCalcMap.put(status, 0);
          }
          Integer count = tmpCalcMap.get(status);
          tmpCalcMap.put(status, count + 1);
        }
      }
      for (Entry<String, Integer> e : tmpCalcMap.entrySet()) {
        result.add(new MetricsResult(e.getKey(), e.getValue()));
      }
    } catch (RedmineException e) {
      throw new MetricsException(e);
    }
    return result;
  }

  private boolean isTargetTracker(Issue issue) {
    if (ignoreTicketTracker == null || ignoreTicketTracker.isEmpty()) {
      return true;
    }
    return !ArrayUtils.contains(ignoreTicketTracker.split(","), issue
        .getTracker().getName());
  }

  private boolean isTargetStatus(Issue issue) {
    if (ignoreTicketStatus == null || ignoreTicketStatus.isEmpty()) {
      return true;
    }
    return !ArrayUtils.contains(ignoreTicketStatus.split(","),
        issue.getStatusName());
  }

  private Project getProject(RedmineManager manager) throws RedmineException {
    List<Project> projects = manager.getProjects();
    for (Project proj : projects) {
      if (projectName.equalsIgnoreCase(proj.getIdentifier())) {
        return proj;
      }
    }
    for (Project proj : projects) {
      if (projectName.equals(proj.getName())) {
        return proj;
      }
    }
    throw new RedmineException("No such project. projectName=" + projectName);
  }

  private List<String> getVersionsString(RedmineManager manager, Project proj)
      throws RedmineException {
    List<Version> allVersions = manager.getVersions(proj.getId());
    if (versions.isEmpty()) {
      return allVersionsWithNull(allVersions);
    }
    List<String> vs = new ArrayList<String>();
    String[] versionStrings = versions.split(",");
    for (String string : versionStrings) {
      for (Version v : allVersions) {
        if (string.trim().equals(v.getName())) {
          vs.add(String.valueOf(v.getId()));
        }
      }
    }
    return vs;
  }

  private List<String> allVersionsWithNull(List<Version> versionList) {
    List<String> vs = new ArrayList<String>();
    vs.add("!*");
    for (Version v : versionList) {
      vs.add(String.valueOf(v.getId()));
    }
    return vs;
  }
}
