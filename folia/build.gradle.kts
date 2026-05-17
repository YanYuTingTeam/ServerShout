dependencies {
    implementation(project(":common"))
    compileOnly("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")
}

val targetJavaVersion = 17
kotlin {
    jvmToolchain(targetJavaVersion)
}