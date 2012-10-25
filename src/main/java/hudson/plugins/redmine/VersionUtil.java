package hudson.plugins.redmine;

public class VersionUtil {

	public static boolean isVersionBefore120(String version) {
		if(version == null) {
			return false; // null is redmine latest version 
		}
		String[] versions = version.split(".");
		if(versions.length == 3 && 
		  Integer.valueOf(versions[0]) <= 1 && 
		  Integer.valueOf(versions[1]) <  2) {
			return true;
		}
		return false;
	}
	
	public static boolean isVersionBefore081(String version) {
		if(version == null) {
			return false; // null is redmine latest version 
		}
		String[] versions = version.split(".");
		if(versions.length == 3 && 
		  Integer.valueOf(versions[0]) == 0 && 
		  Integer.valueOf(versions[1]) <= 8 &&
		  Integer.valueOf(versions[2]) <  1 ) {
			return true;
		}
		return false;
	}
}
