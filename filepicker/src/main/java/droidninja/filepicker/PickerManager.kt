package droidninja.filepicker

import droidninja.filepicker.models.BaseFile
import java.util.ArrayList

import droidninja.filepicker.models.FileType
import droidninja.filepicker.models.sort.SortingTypes
import droidninja.filepicker.utils.Orientation
import java.util.LinkedHashSet

/**
 * Created by droidNinja on 29/07/16.
 */
object PickerManager {
    private var maxCount = FilePickerConst.DEFAULT_MAX_COUNT
    private var showImages = true
    var cameraDrawable = R.drawable.ic_camera
    var sortingType = SortingTypes.none

    val selectedPhotos: ArrayList<String> = ArrayList()
    val selectedFiles: ArrayList<String> = ArrayList()

    private val fileTypes: LinkedHashSet<FileType> = LinkedHashSet()

    var theme : Int = R.style.LibAppTheme

    var title: String? = null

    private var showVideos: Boolean = false

    var isShowGif: Boolean = false

    private var showSelectAll = false

    var isDocSupport = true
        get() = field

    var isEnableCamera = true

    var orientation = Orientation.UNSPECIFIED
        get() = field

    var isShowFolderView = true

    var providerAuthorities: String? = null

    val currentCount: Int
        get() = selectedPhotos.size + selectedFiles.size

    fun setMaxCount(count: Int) {
        reset()
        this.maxCount = count
    }

    fun getMaxCount(): Int {
        return maxCount
    }

    fun add(path: String?, type: Int) {
        if (path != null && shouldAdd()) {
            if (!selectedPhotos.contains(path) && type == FilePickerConst.FILE_TYPE_MEDIA) {
                selectedPhotos.add(path)
            } else if (!selectedFiles.contains(path) && type == FilePickerConst.FILE_TYPE_DOCUMENT) {
                selectedFiles.add(path)
            } else {
                return
            }
        }
    }

    fun add(paths: ArrayList<String>, type: Int) {
        for (index in paths.indices) {
            add(paths[index], type)
        }
    }

    fun remove(path: String?, type: Int) {
        if (type == FilePickerConst.FILE_TYPE_MEDIA && selectedPhotos.contains(path)) {
            selectedPhotos.remove(path)
        } else if (type == FilePickerConst.FILE_TYPE_DOCUMENT) {
            selectedFiles.remove(path)
        }
    }

    fun shouldAdd(): Boolean {
        return if (maxCount == -1) true else currentCount < maxCount
    }

    fun getSelectedFilePaths(files: ArrayList<BaseFile>): ArrayList<String?> {
        val paths = ArrayList<String?>()
        for (index in files.indices) {
            paths.add(files[index].path)
        }
        return paths
    }

    fun reset() {
        selectedFiles.clear()
        selectedPhotos.clear()
        fileTypes.clear()
        maxCount = -1
    }

    fun clearSelections() {
        selectedPhotos.clear()
        selectedFiles.clear()
    }

    fun deleteMedia(paths: ArrayList<String>) {
        selectedPhotos.removeAll(paths)
    }

    fun showVideo(): Boolean {
        return showVideos
    }

    fun setShowVideos(showVideos: Boolean) {
        this.showVideos = showVideos
    }

    fun showImages(): Boolean {
        return showImages
    }

    fun setShowImages(showImages: Boolean) {
        this.showImages = showImages
    }

    fun addFileType(fileType: FileType) {
        fileTypes.add(fileType)
    }

    fun addDocTypes() {
        val pdfs = arrayOf("pdf")
        fileTypes.add(FileType(FilePickerConst.PDF, pdfs, R.drawable.icon_file_pdf))

        val docs = arrayOf("doc", "docx", "dot", "dotx")
        fileTypes.add(FileType(FilePickerConst.DOC, docs, R.drawable.icon_file_doc))

        val ppts = arrayOf("ppt", "pptx")
        fileTypes.add(FileType(FilePickerConst.PPT, ppts, R.drawable.icon_file_ppt))

        val xlss = arrayOf("xls", "xlsx")
        fileTypes.add(FileType(FilePickerConst.XLS, xlss, R.drawable.icon_file_xls))

        val txts = arrayOf("txt")
        fileTypes.add(FileType(FilePickerConst.TXT, txts, R.drawable.icon_file_unknown))
    }

    fun getFileTypes(): ArrayList<FileType> {
        return ArrayList(fileTypes)
    }

    fun hasSelectAll(): Boolean {
        return maxCount == -1 && showSelectAll
    }

    fun enableSelectAll(showSelectAll: Boolean) {
        this.showSelectAll = showSelectAll
    }
}
