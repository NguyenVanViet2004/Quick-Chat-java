pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // add thư viện ImagePicker
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://jitpack.io") }
        jcenter()
    }
}

rootProject.name = "PRO1121-GR!"
include(":app")
