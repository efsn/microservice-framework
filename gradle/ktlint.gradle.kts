val ktlint: Configuration by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:0.35.0")
}

val outputDir = "$buildDir/reports/ktlint"
val inputFiles = project.fileTree(
    "dir" to "src",
    "include" to "**/*.kt"
)
val outputFile = "$outputDir/ktlint-checkstyle-report.xml"

tasks.register<JavaExec>("ktlint") {
    description = "check kotlin code style"
    group = "verification"
    classpath = ktlint

    inputs.files(inputFiles)
    outputs.dir(outputDir)
    main = "com.pinterest.ktlint.Main"
    args(
        "--reporter=plain",
        "--reporter=checkstyle,output=$outputFile",
        "src/**/*.kt"
    )
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fix kotlin code style deviations"
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args("-F", "src/**/*.kt")
}