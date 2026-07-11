plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Sample Extension"
    versionCode = 1
    libVersion = "1.4"
    contentWarning = ContentWarning.SAFE

    source {
        name = "Sample"
        lang = "en"
        baseUrl = "https://example.com"
    }
}
