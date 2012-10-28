package hudson.plugins.redmine;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionUtilTest {

	@Test
	public void testIsVersionBefore120() {
		assertFalse(VersionUtil.isVersionBefore120(""));
		
		assertTrue(VersionUtil.isVersionBefore120("0.9.0"));
		assertTrue(VersionUtil.isVersionBefore120("1.0.0"));
		assertTrue(VersionUtil.isVersionBefore120("1.1.1"));
		assertTrue(VersionUtil.isVersionBefore120("1.1.9"));
		
		assertFalse(VersionUtil.isVersionBefore120("1.2.0"));
		assertFalse(VersionUtil.isVersionBefore120("2.0.0"));
	}
	
	@Test
	public void testIsVersionBefore081() {
		assertFalse(VersionUtil.isVersionBefore081(""));
		
		assertTrue(VersionUtil.isVersionBefore081("0.2.0"));
		assertTrue(VersionUtil.isVersionBefore081("0.7.9"));
		assertTrue(VersionUtil.isVersionBefore081("0.8.0"));
		
		assertFalse(VersionUtil.isVersionBefore081("0.8.2"));
		assertFalse(VersionUtil.isVersionBefore081("1.2.0"));
		assertFalse(VersionUtil.isVersionBefore081("2.0.0"));
	}

}
