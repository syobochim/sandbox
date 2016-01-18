package com.syobochim;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

/**
 * @author syobochim
 */
@Ignore
public class FileCommentMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public TestResources resources = new TestResources();

    @Test
    public void ファイルコメントをつける() throws Exception {
        File baseDir = resources.getBasedir("projects");
        File pom = new File(baseDir, "pom.xml");

        Mojo mojo = mojoRule.lookupMojo("help", pom);
        mojo.execute();
    }

}