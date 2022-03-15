package droidninja.filepicker.cursors.loadercallbacks

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader

import droidninja.filepicker.PickerManager
import droidninja.filepicker.cursors.PhotoDirectoryLoader
import droidninja.filepicker.models.PhotoDirectory
import java.lang.ref.WeakReference
import java.util.ArrayList

import android.provider.BaseColumns._ID
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE
import android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
import android.provider.MediaStore.Images.ImageColumns.BUCKET_ID
import android.provider.MediaStore.MediaColumns.DATA
import android.provider.MediaStore.MediaColumns.DATE_ADDED
import android.provider.MediaStore.MediaColumns.TITLE

class PhotoDirLoaderCallbacks(context: Context,
                              private val resultCallback: FileResultCallback<PhotoDirectory>?) : LoaderManager.LoaderCallbacks<Cursor> {
    private val context: WeakReference<Context>

    init {
        this.context = WeakReference(context)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return PhotoDirectoryLoader(context.get(), args!!)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {

        if (data == null) return
        val directories = ArrayList<PhotoDirectory>()

        while (data.moveToNext()) {

            val imageId = data.getInt(data.getColumnIndexOrThrow(_ID))
            val bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID))
            val name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME))
            val path = data.getString(data.getColumnIndexOrThrow(DATA))
            val fileName = data.getString(data.getColumnIndexOrThrow(TITLE))
            val mediaType = data.getInt(data.getColumnIndexOrThrow(MEDIA_TYPE))

            val photoDirectory = PhotoDirectory()
            photoDirectory.bucketId = bucketId
            photoDirectory.setName(name)

            if (!directories.contains(photoDirectory)) {
                if (path != null && path.toLowerCase().endsWith("gif")) {
                   /* if (PickerManager.getInstance().isShowGif()) {
                        photoDirectory.addPhoto(imageId, fileName, path, mediaType)
                    }*/
                } else {
                    photoDirectory.addPhoto(imageId, fileName, path, mediaType)
                }

                photoDirectory.dateAdded = data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))
                directories.add(photoDirectory)
            } else {
                directories[directories.indexOf(photoDirectory)]
                        .addPhoto(imageId, fileName, path, mediaType)
            }
        }

        resultCallback?.onResultCallback(directories)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    companion object {
        val INDEX_ALL_PHOTOS = 0
    }
}