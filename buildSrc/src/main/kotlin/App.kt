import org.gradle.api.Project

object App {

    const val minSdk = 21
    const val compileSdk = 35

    const val appNamespace = "com.cinnamon.cleanexplorers"
    const val appApplicationId = "blooom.cinnamon.cleanexplorers"
    const val appVersionName = "1.0"
    const val appVersionCode = 1

    fun getDebugVersionNameSuffix(project: Project): String {
        return ".${getGitCommitHash(project)}.${getGitBranchName(project)}.${
            getGitFileChanged(project)
        }"
    }

    private fun getGitCommitHash(project: Project): String =
        executeCommand(project, "git rev-parse --short HEAD")

    private fun getGitBranchName(project: Project): String =
        executeCommand(project, "git rev-parse --abbrev-ref HEAD")

    private fun getGitFileChanged(project: Project): String {
        val git = executeCommand(project, "git status --porcelain")
        val numberOfChanges = git.lines().size
        return if (git.isEmpty() || numberOfChanges == 0)
            "no-changes"
        else
            "$numberOfChanges-changes"
    }

    private fun executeCommand(project: Project, command: String): String =
        project.providers.exec {
            commandLine(command.split(" "))
        }.standardOutput.asText.get().trim()

}
