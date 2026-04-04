package com.garbagecollection.app.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object IncidentPhotoManager {

    fun createPhotoUri(context: Context): Uri {
        val photoDirectory = File(context.filesDir, "incident_photos").apply {
            mkdirs()
        }
        val photoFile = File(photoDirectory, "incident_${System.currentTimeMillis()}.jpg")

        return runCatching {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
        }.getOrElse {
            Uri.fromFile(photoFile)
        }
    }
}
