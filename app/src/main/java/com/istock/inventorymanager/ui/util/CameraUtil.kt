package com.istock.inventorymanager.ui.util

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraUtil {
    
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "ITEM_$timeStamp"
        val storageDir = File(context.filesDir, "item_images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, "$imageFileName.jpg")
    }
    
    fun getImageUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    fun captureImage(
        imageCapture: ImageCapture,
        outputFile: File,
        onSuccess: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        
        imageCapture.takePicture(
            outputOptions,
            androidx.camera.core.impl.utils.executor.CameraXExecutors.mainThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSuccess(Uri.fromFile(outputFile))
                }
                
                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }
}
