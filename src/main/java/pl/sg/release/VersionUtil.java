package pl.sg.release;

import pl.sg.runner.CommandRunner;
import pl.sg.runner.ProcessResult;

import java.io.File;
import java.util.List;

public class VersionUtil {

    private VersionUtil() {
    }

    public static String getNextMajorVersion(File workingDir) {
        Version version = getCurrentVersion(workingDir);
        return new Version(version.major() + 1, 0, 0).toString();
    }

    public static String getNextMinorVersion(File workingDir) {
        Version version = getCurrentVersion(workingDir);
        return new Version(version.major(), version.minor() + 1, 0).toString();
    }

    public static String getNextPatchVersion(File workingDir) {
        Version version = getCurrentVersion(workingDir);
        return new Version(version.major(), version.minor(), version.patch() + 1).toString();
    }

    public static String getNextVersion(File workingDir, String level) {
        return switch (level) {
            case "patch" -> getNextPatchVersion(workingDir);
            case "minor" -> getNextMinorVersion(workingDir);
            case "major" -> getNextMajorVersion(workingDir);
            default -> throw new IllegalArgumentException("Unknown level: " + level);
        };
    }

    public static Version getCurrentVersion(File workingDir) {
        executeCommandAndAssertSucceeded(workingDir, List.of("git", "fetch", "--tag"));
        ProcessResult processResult = executeCommandAndAssertSucceeded(workingDir, List.of("git", "tag", "-l"));
        return processResult
                .output()
                .stream()
                .filter(Version::validateFormat)
                .map(Version::parse)
                .reduce((version, version2) -> version.compareTo(version2) >= 0 ? version : version2)
                .orElseThrow();
    }

    private static ProcessResult executeCommandAndAssertSucceeded(File workingDir, List<String> command) {
        ProcessResult processResult = CommandRunner.runCommand(command, false, workingDir);
        if (processResult.exitCode() != 0) {
            throw new RuntimeException("Error executing command: " + String.join(" ", command) + ": " + String.join("\n", processResult.error()));
        }
        return processResult;
    }
}
