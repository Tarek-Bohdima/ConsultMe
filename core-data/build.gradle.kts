plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.thecompany.consultme.core.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }

    lint {
        baseline = file("lint-baseline.xml")
        // --- Add these lines to be more explicit ---
        quiet = true
        checkAllWarnings = true

        // This is a strict setting that will elevate all warnings to errors.
        // This will force them to appear in the report.
        // You may want to set this to 'false' again later.
        warningsAsErrors = false

        // --- Keep the previous settings ---
        textReport = true
        htmlReport = true
        xmlReport = false
        checkReleaseBuilds = true
        abortOnError = true
        checkDependencies = true
    }
}

dependencies {

    implementation(project(":core-database"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
