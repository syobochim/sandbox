package com.syobochim;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Add file comment.
 */
@Mojo( name = "touch", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class FileCommentMojo extends AbstractMojo{

    @Parameter( property = "srcDir", required = true )
    private File inputDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Path path = inputDir.toPath();

        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                List<String> fileComments = new ArrayList<String>();
                fileComments.add("/ *");
                fileComments.add("  *");
                fileComments.add("  */");

                List<String> lines = Files.readAllLines(file);
                lines.addAll(0, fileComments);

                Files.write(path, lines);
                return FileVisitResult.CONTINUE;
            }
        };
    }
}
