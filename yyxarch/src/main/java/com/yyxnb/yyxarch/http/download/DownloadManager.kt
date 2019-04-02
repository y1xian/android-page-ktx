package com.yyxnb.yyxarch.http.download


import com.yyxnb.yyxarch.AppUtils
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * 保存下载的文件
 */

class DownloadManager {


    /**
     * 保存文件
     *
     * @param response     ResponseBody
     * @param destFileName 文件名（包括文件后缀）
     * @return 返回
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveFile(response: ResponseBody, destFileName: String, IProgressListener: IProgressListener): File {

        val destFileDir = AppUtils.context.getExternalFilesDir(null)!!.toString() + File.separator

        val contentLength = response.contentLength()
        var input: InputStream? = null
        val buf = ByteArray(1024 * 4)
        var len = 0
        var fos: FileOutputStream? = null
        try {
            input = response.byteStream()

            var sum: Long = 0

            val dir = File(destFileDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, destFileName)
            fos = FileOutputStream(file)
//            while ((len = input!!.read(buf)) != -1) {
            while (input!!.read(buf).apply { len = this } > 0) {
                sum += len.toLong()
                fos.write(buf, 0, len)

                val finalSum = sum

                IProgressListener.onResponseProgress(finalSum, contentLength, (finalSum * 1.0f / contentLength * 100).toInt(), finalSum == contentLength, file.absolutePath)
            }
            fos.flush()

            return file

        } finally {
            try {
                response.close()
                input?.close()
            } catch (e: IOException) {
            }

            try {
                fos?.close()
            } catch (e: IOException) {
            }

        }
    }


}
