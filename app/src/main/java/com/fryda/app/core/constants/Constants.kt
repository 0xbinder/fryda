package com.fryda.app.core.constants

object Constants {
    const val BASE_URL = "https://api.github.com/"

    // Retrofit Endpoints
    const val RELEASES_ENDPOINT = "repos/frida/frida/releases"
    const val RELEASE_BY_TAG_ENDPOINT = "repos/frida/frida/releases/tags/{tag}"
    const val LATEST_RELEASE_ENDPOINT = "repos/frida/frida/releases/latest"
}