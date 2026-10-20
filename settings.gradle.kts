pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://github.com/QuickBlox/android-ui-kit-releases/raw/master/") }
        maven { url = uri("https://github.com/QuickBlox/quickblox-android-sdk-releases/raw/master/") }
        maven { url = uri("https://github.com/QuickBlox/android-ai-releases/raw/main/") }
    }
}

rootProject.name = "Tenee"
include(":app")
 