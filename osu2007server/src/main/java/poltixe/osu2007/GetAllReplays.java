package poltixe.osu2007;

import static java.nio.file.FileVisitResult.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.concurrent.TimeUnit;
import java.nio.*;

public class GetAllReplays extends SimpleFileVisitor<Path> {
    // Print information about
    // each type of file.
    MySqlHandler sqlHandler = new MySqlHandler();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (attr.isSymbolicLink()) {
            // System.out.format("Symbolic link: %s ", file);
        } else if (attr.isRegularFile()) {
            // System.out.format("Regular file: %s ", file);
            // System.out.format("Modification time: %s ",
            // attr.creationTime().to(TimeUnit.SECONDS));
            sqlHandler.setTimeOfScore(
                    Integer.parseInt(
                            file.getFileName().toString().substring(0, file.getFileName().toString().length() - 4)),
                    attr.creationTime().to(TimeUnit.SECONDS));
        } else {
            // System.out.format("Other: %s ", file);
        }
        // System.out.println("(" + attr.size() + "bytes)");
        return CONTINUE;
    }

    // Print each directory visited.
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.format("Directory: %s%n", dir);
        return CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }

}
