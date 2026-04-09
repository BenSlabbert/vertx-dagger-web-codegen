rootProject.name = "advice-extractor-gradle-plugin"

// Make advice-extractor-core available for dependency substitution so that the plugin can be
// built and tested standalone (without the parent composite build).
includeBuild("../advice-extractor-core")
