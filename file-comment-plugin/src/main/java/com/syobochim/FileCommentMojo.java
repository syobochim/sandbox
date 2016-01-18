package com.syobochim;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Add file comment.
 */
@Mojo(name = "addComment", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FileCommentMojo extends AbstractMojo {

    @Parameter(required = true)
    private File inputDir;

    @Parameter(required = true)
    private String comment;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Path path = inputDir.toPath();

        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                List<String> fileComments = new ArrayList<>();
                fileComments.add("/*");
                fileComments.add(" * " + comment);
                fileComments.add(" */");

                List<String> lines = Files.readAllLines(file);
                lines.addAll(0, fileComments);

                Files.write(file, lines);
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(path, visitor);
        } catch (IOException e) {
            getLog().error("This is error level, Maven will print this even if user uses -q option.");
            e.printStackTrace();
        }
    }
}
