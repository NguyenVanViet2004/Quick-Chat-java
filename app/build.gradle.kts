plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.pro1121_gr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pro1121_gr"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    viewBinding {
        enable = true
    }
    buildFeatures{
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.firebase:firebase-messaging:23.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")

    // country code picker android
    implementation ("com.hbb20:ccp:2.5.0")

    val lottieVersion = "3.4.0"
    implementation ("com.airbnb.android:lottie:$lottieVersion")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    // load image form url
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    // circle avatar
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    // thư viện quản lý quyền
    implementation ("com.karumi:dexter:6.2.3")
    // ui firestore
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")

    // add thư viện imagePicker
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation ("com.github.qamarelsafadi:CurvedBottomNavigation:0.1.3")


    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
    implementation ("com.etebarian:meow-bottom-navigation:1.2.0")
    // toasty library
    implementation ("com.github.GrenderG:Toasty:1.5.2")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // add library voice/video call
    implementation ("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+")
    implementation ("com.github.ZEGOCLOUD:zego_uikit_signaling_plugin_android:+")
    // chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


}