package org.eclipse.mylyn.github.tests.ui;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubRepositoryUrlBuilder;
import org.eclipse.mylyn.github.ui.internal.GitHubRepositoryConnectorUI;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;

/**
 * Test the Repository Connector UI functionality.
 * 
 * @author Gabriel Ciuloaica (gciuloaica@gmail.com)
 * 
 */
@SuppressWarnings("restriction")
public class GitHubRepositoryConnectorUITest {

	private static final int REGION_LENGTH = 2;
	private static final int HYPERLIK_REGION_OFFSET = 8;
	private static final int OFFSET = 4;
	private final GitHubRepositoryConnectorUI connectorUI;
	private final TaskRepository repository;

	/**
	 * Create a new instance of the test.
	 */
	public GitHubRepositoryConnectorUITest() {
		connectorUI = new GitHubRepositoryConnectorUI();
		repository = new TaskRepository(GitHub.CONNECTOR_KIND,
				GitHubRepositoryUrlBuilder.buildGitHubUrl("foo", "bar"));
	}

	/**
	 * Find hyperlinks test.
	 */
	@Test
	public final void testFindHyperlinksTaskRepositoryStringIntInt() {
		IHyperlink[] hyperlinks = connectorUI.findHyperlinks(repository,
				"one #2 three", -1, 0);
		assertNotNull(hyperlinks);
		assertEquals(1, hyperlinks.length);
		assertEquals(new Region(OFFSET, REGION_LENGTH),
				hyperlinks[0].getHyperlinkRegion());

		hyperlinks = connectorUI.findHyperlinks(repository, "one #2 three", -1,
				OFFSET);
		assertNotNull(hyperlinks);
		assertEquals(1, hyperlinks.length);
		assertEquals(new Region(HYPERLIK_REGION_OFFSET, REGION_LENGTH),
				hyperlinks[0].getHyperlinkRegion());
	}

}
