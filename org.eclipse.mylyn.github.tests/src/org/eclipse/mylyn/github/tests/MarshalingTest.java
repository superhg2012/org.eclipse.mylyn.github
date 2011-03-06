package org.eclipse.mylyn.github.tests;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.eclipse.mylyn.github.internal.GitHubIssue;
import org.eclipse.mylyn.github.internal.GitHubIssues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gson.Gson;

@SuppressWarnings("restriction")
@RunWith(JUnit4.class)
public class MarshalingTest {

	private static final int INDEX_OF_LAST_ISSUE_FROM_LIST = 9;
	private static final int NUMBER_OF_EXPECTED_ISSUES = 10;
	private static final int NUMBER_OF_COMMENTS = 5;
	private Gson gson;

	@Before
	public final void beforeTest() {
		gson = new Gson();
	}

	@Test
	public final void unmarshalIssues() throws IOException {
		GitHubIssues issues = gson.fromJson(
				getResource("resources/issues.json"), GitHubIssues.class);

		assertTrue(issues != null);
		assertTrue(issues.getIssues() != null);

		assertEquals(NUMBER_OF_EXPECTED_ISSUES, issues.getIssues().size());

		GitHubIssue issue = (issues.getIssues().toArray(new GitHubIssue[0]))[INDEX_OF_LAST_ISSUE_FROM_LIST];
		// {"number":10,"votes":0,"created_at":"2010/02/04 21:03:54 -0800","body":"test description 2 ","title":"test issue for testing mylyn github connector2",
		// "updated_at":"2010/02/04 21:09:37 -0800","closed_at":null,"user":"dgreen99","labels":[],"state":"open"}]}
		assertEquals("10", issue.getNumber());
		assertEquals("2010/02/04 21:03:54 -0800", issue.getCreatedAt());
		assertEquals("test description 2 ", issue.getBody());
		assertEquals("test issue for testing mylyn github connector2",
				issue.getTitle());
		assertEquals("2010/02/04 21:09:37 -0800", issue.getUpdatedAt());
		assertNull(issue.getClosedAt());
		assertEquals("dgreen99", issue.getUser());
		assertEquals("open", issue.getState());
		assertTrue(issue.getLabels().isEmpty());
		assertTrue(issue.getComments() == NUMBER_OF_COMMENTS);
	}

	private String getResource(String resource) throws IOException {

		InputStream stream = null;
		Reader reader = null;
		try {
			stream = MarshalingTest.class.getResourceAsStream(resource);
			StringWriter writer = new StringWriter();
			reader = new InputStreamReader(stream);
			int c;
			while ((c = reader.read()) != -1) {
				writer.write(c);
			}
			return writer.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (stream != null) {
				stream.close();
			}
		}

	}
}
