package hudson.plugins.redmine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.NonStrictExpectations;

import org.junit.Test;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Version;

public class RedmineMetricsCalculatorTest {

  @Test
  public void testCalc() throws MetricsException, RedmineException {
    new NonStrictExpectations() {
      RedmineManager redmineManager;
      {
        redmineManager.getProjects();
        ArrayList<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setId(1);
        p.setName("Example");
        projects.add(p);
        returns(projects);

        redmineManager.getVersions(p.getId());
        ArrayList<Version> versions = new ArrayList<Version>();
        Version v = new Version();
        v.setId(1);
        v.setName("v1");
        versions.add(v);
        returns(versions);

        Map<String, String> params = new HashMap<String, String>();
        params.put("project_id", p.getId().toString());
        params.put("fixed_version_id", "1");
        params.put("status_id", "*");
        redmineManager.getIssues(params);
        List<Issue> issues = new ArrayList<Issue>();
        Issue issue = new Issue();
        issue.setStatusId(1);
        issue.setId(1);
        issue.setSubject("Hello");
        issue.setStatusName("Open");
        issues.add(issue);
        returns(issues);
      }
    };

    RedmineMetricsCalculator rmc = new RedmineMetricsCalculator(
        "http://example.com/", "APIKEY", "Example", "v1", "", "");
    assertEquals(1, rmc.calc().size());
  }

  @Test(expected=MetricsException.class)
  public void testNoSuchProject() throws MetricsException, RedmineException {
    new NonStrictExpectations() {
      RedmineManager redmineManager;
      {
        redmineManager.getProjects();
        ArrayList<Project> projects = new ArrayList<Project>();
        Project p = new Project();
        p.setId(1);
        p.setName("Example");
        projects.add(p);
        returns(projects);
      }
    };

    RedmineMetricsCalculator rmc = new RedmineMetricsCalculator(
        "http://example.com/", "APIKEY", "NoSuchProject", "v1", "", "");
    rmc.calc();
  }

}
