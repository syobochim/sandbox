package com.syobochim;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author syobochim
 */
public class FileCommentMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public TestResources resources = new TestResources();

    @Test
    public void ファイルコメントをつける() throws Exception {
        File baseDir = resources.getBasedir("java-se-seven-and-eight");
        mojoRule.executeMojo(baseDir, "addComment");

        Path sampleJava = baseDir.toPath().resolve(Paths.get("src", "main", "java", "com",
                "syobochim", "lambda", "LambdaSample.java"));
        List<String> comment = Files.readAllLines(sampleJava).stream().limit(3)
                .collect(Collectors.toList());

        List<String> expected = Arrays.asList("/*", " * syobochim", " */");

        assertThat(comment, is(expected));
    }

}