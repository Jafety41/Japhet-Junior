package com.example.data

object AppwriteStorageHelper {
    private const val ENDPOINT = "https://fra.cloud.appwrite.io/v1"
    private const val PROJECT_ID = "69cfb69f002b2ff39dc9"
    private const val BUCKET_ID = "materials"

    // Construct the view URL (loads inline directly in PDF and web viewers)
    fun getFileViewUrl(fileId: String): String {
        return "$ENDPOINT/storage/buckets/$BUCKET_ID/files/$fileId/view?project=$PROJECT_ID"
    }

    // Construct the download URL
    fun getFileDownloadUrl(fileId: String): String {
        return "$ENDPOINT/storage/buckets/$BUCKET_ID/files/$fileId/download?project=$PROJECT_ID"
    }
}
