
import com.google.gson.Gson
import gradlebuild.basics.releasedVersionsFile
import gradlebuild.buildutils.model.ReleasedVersion
import gradlebuild.buildutils.tasks.UpdateAgpVersions
import gradlebuild.buildutils.tasks.UpdateKotlinVersions
import gradlebuild.buildutils.tasks.UpdateReleasedVersions
import java.net.URL


tasks.withType<UpdateReleasedVersions>().configureEach {
    releasedVersionsFile = releasedVersionsFile()
    group = "Versioning"
}

tasks.register<UpdateReleasedVersions>("updateReleasedVersions") {
    currentReleasedVersion = ReleasedVersion(
        project.findProperty("currentReleasedVersion").toString(),
        project.findProperty("currentReleasedVersionBuildTimestamp").toString()
    )
}

tasks.register<UpdateReleasedVersions>("updateReleasedVersionsToLatestNightly") {
    currentReleasedVersion = project.provider {
        val jsonText = URL("https://services.gradle.org/versions/nightly").readText()
        println(jsonText)
        val versionInfo = Gson().fromJson(jsonText, VersionBuildTimeInfo::class.java)
        ReleasedVersion(versionInfo.version, versionInfo.buildTime)
    }
}

tasks.register<UpdateAgpVersions>("updateAgpVersions") {
    comment = " Generated - Update by running `./gradlew updateAgpVersions`"
    minimumSupportedMinor = "7.3"
    propertiesFile = layout.projectDirectory.file("gradle/dependency-management/agp-versions.properties")
    compatibilityDocFile = layout.projectDirectory.file("subprojects/docs/src/docs/userguide/compatibility.adoc")
}

tasks.register<UpdateKotlinVersions>("updateKotlinVersions") {
    comment = " Generated - Update by running `./gradlew updateKotlinVersions`"
    minimumSupported = "1.6.10"
    propertiesFile = layout.projectDirectory.file("gradle/dependency-management/kotlin-versions.properties")
    compatibilityDocFile = layout.projectDirectory.file("subprojects/docs/src/docs/userguide/compatibility.adoc")
}

data class VersionBuildTimeInfo(val version: String, val buildTime: String)
