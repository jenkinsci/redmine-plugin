package hudson.plugins.redmine;

import hudson.MarkupText;
import hudson.plugins.redmine.RedmineLinkAnnotator;
import junit.framework.TestCase;

public class RedmineLinkAnnotatorTest extends TestCase {

	private static final String REDMINE_URL = "http://local.redmine/";

    public void testWikiLinkSyntax() {
        assertAnnotatedTextEquals("Nothing here.", "Nothing here.");
        assertAnnotatedTextEquals("Text with WikiLink.", "Text with <a href='" + REDMINE_URL + "wiki/WikiLink'>WikiLink</a>.");
        assertAnnotatedTextEquals("#42", "<a href='" + REDMINE_URL + "issues/show/42'>#42</a>");
        assertAnnotatedTextEquals("IssueID 22", "<a href='" + REDMINE_URL + "issues/show/22'>IssueID 22</a>");
        assertAnnotatedTextEquals("fixes 10,11,12", 
        						  "<a href='" + REDMINE_URL + "issues/show/10'>fixes 10</a>," +
        						  "<a href='" + REDMINE_URL + "issues/show/11'>11</a>," +
        						  "<a href='" + REDMINE_URL + "issues/show/12'>12</a>");
        assertAnnotatedTextEquals("references 110,111,112,113", 
        						  "<a href='" + REDMINE_URL + "issues/show/110'>references 110</a>," +
        						  "<a href='" + REDMINE_URL + "issues/show/111'>111</a>," +
        						  "<a href='" + REDMINE_URL + "issues/show/112'>112</a>," +
        						  "<a href='" + REDMINE_URL + "issues/show/113'>113</a>");
        assertAnnotatedTextEquals("closes 210, 211", 
        						  "<a href='" + REDMINE_URL + "issues/show/210'>closes 210</a>, " +
        						  "<a href='" + REDMINE_URL + "issues/show/211'>211</a>");
        assertAnnotatedTextEquals("closes 210 211", 
        						  "<a href='" + REDMINE_URL + "issues/show/210'>closes 210</a> " +
				                  "<a href='" + REDMINE_URL + "issues/show/211'>211</a>");
        assertAnnotatedTextEquals("refs 310, 11, 4, 4120", 
        						  "<a href='" + REDMINE_URL + "issues/show/310'>refs 310</a>, " +
        						  "<a href='" + REDMINE_URL + "issues/show/11'>11</a>, " +
        						  "<a href='" + REDMINE_URL + "issues/show/4'>4</a>, " +
        						  "<a href='" + REDMINE_URL + "issues/show/4120'>4120</a>");
        assertAnnotatedTextEquals("refs 1&amp;11&amp;111&amp;1111", 
        						  "<a href='" + REDMINE_URL + "issues/show/1'>refs 1</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/show/11'>11</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/show/111'>111</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/show/1111'>1111</a>");
        assertAnnotatedTextEquals("IssueID 21&amp;11&amp;100", 
        						  "<a href='" + REDMINE_URL + "issues/show/21'>IssueID 21</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/show/11'>11</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/show/100'>100</a>");
        assertAnnotatedTextEquals("refs #1,#11,#111,#1111", 
			                      "<a href='" + REDMINE_URL + "issues/show/1'>refs #1</a>," +
				  				  "<a href='" + REDMINE_URL + "issues/show/11'>#11</a>," +
				                  "<a href='" + REDMINE_URL + "issues/show/111'>#111</a>," +
				                  "<a href='" + REDMINE_URL + "issues/show/1111'>#1111</a>");
        assertAnnotatedTextEquals("refs #1, #11, #111,#1111", 
                                  "<a href='" + REDMINE_URL + "issues/show/1'>refs #1</a>, " +
				                  "<a href='" + REDMINE_URL + "issues/show/11'>#11</a>, " +
                                  "<a href='" + REDMINE_URL + "issues/show/111'>#111</a>," +
                                  "<a href='" + REDMINE_URL + "issues/show/1111'>#1111</a>");
        assertAnnotatedTextEquals("refs #1", 
        						  "<a href='" + REDMINE_URL + "issues/show/1'>refs #1</a>");
        assertAnnotatedTextEquals("closes #1&amp;#11", 
                                  "<a href='" + REDMINE_URL + "issues/show/1'>closes #1</a>&amp;" +
                                  "<a href='" + REDMINE_URL + "issues/show/11'>#11</a>");
        assertAnnotatedTextEquals("closes #1", 
                			      "<a href='" + REDMINE_URL + "issues/show/1'>closes #1</a>");
        assertAnnotatedTextEquals("IssueID #1 #11", 
                                  "<a href='" + REDMINE_URL + "issues/show/1'>IssueID #1</a> " +
                                  "<a href='" + REDMINE_URL + "issues/show/11'>#11</a>");
    }

    private void assertAnnotatedTextEquals(String originalText, String expectedAnnotatedText) {
        MarkupText markupText = new MarkupText(originalText);
        for (RedmineLinkAnnotator.LinkMarkup markup : RedmineLinkAnnotator.MARKUPS) {
            markup.process(markupText, REDMINE_URL);
        }

        System.out.println(markupText.toString());
        assertEquals(expectedAnnotatedText, markupText.toString());
    }
}
