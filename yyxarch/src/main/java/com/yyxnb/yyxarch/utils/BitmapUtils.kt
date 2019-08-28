package com.yyxnb.yyxarch.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.PorterDuff.Mode
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.view.View
import com.yyxnb.yyxarch.utils.log.LogUtils
import java.io.*


/**
 * Bitmap工具类主要包括获取Bitmap和对Bitmap的操作
 */
object BitmapUtils : Serializable {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 模糊效果处理
     *
     * @return 模糊效果处理后的图片
     */
    fun blur(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565)

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        var newColor = 0

        val colors = Array(9) { IntArray(3) }
        var i = 1
        val length = width - 1
        while (i < length) {
            var k = 1
            val len = height - 1
            while (k < len) {
                for (m in 0..8) {
                    var s = 0
                    var p = 0
                    when (m) {
                        0 -> {
                            s = i - 1
                            p = k - 1
                        }
                        1 -> {
                            s = i
                            p = k - 1
                        }
                        2 -> {
                            s = i + 1
                            p = k - 1
                        }
                        3 -> {
                            s = i + 1
                            p = k
                        }
                        4 -> {
                            s = i + 1
                            p = k + 1
                        }
                        5 -> {
                            s = i
                            p = k + 1
                        }
                        6 -> {
                            s = i - 1
                            p = k + 1
                        }
                        7 -> {
                            s = i - 1
                            p = k
                        }
                        8 -> {
                            s = i
                            p = k
                        }
                    }
                    pixColor = bitmap.getPixel(s, p)
                    colors[m][0] = Color.red(pixColor)
                    colors[m][1] = Color.green(pixColor)
                    colors[m][2] = Color.blue(pixColor)
                }

                for (m in 0..8) {
                    newR += colors[m][0]
                    newG += colors[m][1]
                    newB += colors[m][2]
                }

                newR = (newR / 9f).toInt()
                newG = (newG / 9f).toInt()
                newB = (newB / 9f).toInt()

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                newColor = Color.argb(255, newR, newG, newB)
                newBitmap.setPixel(i, k, newColor)

                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }
        return newBitmap
    }


    /**
     * 图片压缩处理（使用Options的方法）
     *
     * **说明**使用方法：
     * 首先你要将Options的inJustDecodeBounds属性设置为true，BitmapFactory.decode一次图片 。
     * 然后将Options连同期望的宽度和高度一起传递到到本方法中。
     * 之后再使用本方法的返回值做参数调用BitmapFactory.decode创建图片。
     *
     * **说明**BitmapFactory创建bitmap会尝试为已经构建的bitmap分配内存
     * ，这时就会很容易导致OOM出现。为此每一种创建方法都提供了一个可选的Options参数
     * ，将这个参数的inJustDecodeBounds属性设置为true就可以让解析方法禁止为bitmap分配内存
     * ，返回值也不再是一个Bitmap对象， 而是null。虽然Bitmap是null了，但是Options的outWidth、
     * outHeight和outMimeType属性都会被赋值。
     *
     * @param reqWidth  目标宽度,这里的宽高只是阀值，实际显示的图片将小于等于这个值
     * @param reqHeight 目标高度,这里的宽高只是阀值，实际显示的图片将小于等于这个值
     */
    fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int,
            reqHeight: Int): BitmapFactory.Options {
        // 源图片的高度和宽度
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > 400 || width > 450) {
            if (height > reqHeight || width > reqWidth) {
                // 计算出实际宽高和目标宽高的比率
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
                // 一定都会大于等于目标的宽和高。
                inSampleSize = if (heightRatio < widthRatio)
                    heightRatio
                else
                    widthRatio
            }
        }
        // 设置压缩比例
        options.inSampleSize = inSampleSize
        options.inJustDecodeBounds = false
        return options
    }

    /**
     * 获取一个指定大小的bitmap
     *
     * @param res       Resources
     * @param resId     图片ID
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     */
    fun getBitmapFromResource(res: Resources, resId: Int,
                              reqWidth: Int, reqHeight: Int): Bitmap? {
        // BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        // options = BitmapHelper.calculateInSampleSize(options, reqWidth,
        // reqHeight);
        // return BitmapFactory.decodeResource(res, resId, options);

        // 通过JNI的形式读取本地图片达到节省内存的目的
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Config.RGB_565
        options.inPurgeable = true
        options.inInputShareable = true
        val `is` = res.openRawResource(resId)
        return getBitmapFromStream(`is`, null, reqWidth, reqHeight)
    }

    /**
     * 获取一个指定大小的bitmap
     *
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     */
    fun getBitmapFromFile(pathName: String, reqWidth: Int,
                          reqHeight: Int): Bitmap {
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        options = calculateInSampleSize(options, reqWidth, reqHeight)
        return BitmapFactory.decodeFile(pathName, options)
    }

    /**
     * 获取一个指定大小的bitmap
     *
     * @param data      Bitmap的byte数组
     * @param offset    image从byte数组创建的起始位置
     * @param length    the number of bytes, 从offset处开始的长度
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     */
    fun getBitmapFromByteArray(data: ByteArray, offset: Int,
                               length: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, offset, length, options)
        options = calculateInSampleSize(options, reqWidth, reqHeight)
        return BitmapFactory.decodeByteArray(data, offset, length, options)
    }

    /**
     * 把bitmap转化为bytes
     * Bitmap → byte[]
     *
     * @param bitmap 源Bitmap
     * @return Byte数组
     */
    fun getBytesFromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    /**
     * Stream转换成Byte
     *
     * @param inputStream InputStream
     * @return Byte数组
     */
    fun getBytesFromStream(inputStream: InputStream): ByteArray {
        val os = ByteArrayOutputStream(1024)
        val buffer = ByteArray(1024)
        var len: Int
        try {
//                while ((len = inputStream.read(buffer)) >= 0) {
            while ((inputStream.read(buffer).apply { len = this }) >= 0) {
                os.write(buffer, 0, len)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return os.toByteArray()
    }

    /**
     * 获取一个指定大小的bitmap
     * byte[] → Bitmap
     *
     * @param b Byte数组
     * @return 需要的Bitmap
     */
    fun getBitmapFromBytes(b: ByteArray): Bitmap? {
        return if (b.isNotEmpty()) {
            BitmapFactory.decodeByteArray(b, 0, b.size)
        } else {
            null
        }
    }


    /**
     * 获取一个指定大小的bitmap
     *
     * @param is         从输入流中读取Bitmap
     * @param outPadding If not null, return the padding rect for the bitmap if it
     * exists, otherwise set padding to [-1,-1,-1,-1]. If no bitmap
     * is returned (null) then padding is unchanged.
     * @param reqWidth   目标宽度
     * @param reqHeight  目标高度
     */
    fun getBitmapFromStream(`is`: InputStream, outPadding: Rect?,
                            reqWidth: Int, reqHeight: Int): Bitmap? {
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(`is`, outPadding, options)
        options = calculateInSampleSize(options, reqWidth, reqHeight)
        return BitmapFactory.decodeStream(`is`, outPadding, options)
    }

    /**
     * 从View获取Bitmap
     *
     * @param view View
     * @return Bitmap
     */
    fun getBitmapFromView(view: View?): Bitmap? {
        if (view == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(view.width, view.height,
                Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        view.layout(view.left, view.top, view.right,
                view.bottom)
        view.draw(canvas)

        return bitmap
    }

    /**
     * 把一个View的对象转换成bitmap
     *
     * @param view View
     * @return Bitmap
     */
    fun getBitmapFromView2(view: View?): Bitmap? {
        if (view == null) {
            return null
        }
        view.clearFocus()
        view.isPressed = false

        // 能画缓存就返回false
        val willNotCache = view.willNotCacheDrawing()
        view.setWillNotCacheDrawing(false)
        val color = view.drawingCacheBackgroundColor
        view.drawingCacheBackgroundColor = 0
        if (color != 0) {
            view.destroyDrawingCache()
        }
        view.buildDrawingCache()
        val cacheBitmap = view.drawingCache
        if (cacheBitmap == null) {
            LogUtils.e("failed getViewBitmap(" + view + ") -->" +
                    RuntimeException())
            return null
        }
        val bitmap = Bitmap.createBitmap(cacheBitmap)
        // Restore the view
        view.destroyDrawingCache()
        view.setWillNotCacheDrawing(willNotCache)
        view.drawingCacheBackgroundColor = color
        return bitmap
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        // 取 drawable 的长宽
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        // 取 drawable 的颜色格式
        val config = if (drawable.opacity != PixelFormat.OPAQUE)
            Config.ARGB_8888
        else
            Config.RGB_565
        // 建立对应 bitmap
        val bitmap = Bitmap.createBitmap(w, h, config)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 将 Bitmap转化为Drawable
     *
     * @param bm Bitmap
     * @return Drawable
     */
    fun getDrawableFromBitmap(bm: Bitmap?): Drawable? {
        return if (bm == null) {
            null
        } else BitmapDrawable(bm)
    }

    /**
     * 合并Bitmap
     *
     * @param bgd 背景Bitmap
     * @param fg  前景Bitmap
     * @return 合成后的Bitmap
     */
    fun combineImages(bgd: Bitmap, fg: Bitmap): Bitmap {
        val bmp: Bitmap

        val width = if (bgd.width > fg.width)
            bgd.width
        else
            fg
                    .width
        val height = if (bgd.height > fg.height)
            bgd.height
        else
            fg
                    .height

        bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(Mode.SRC_ATOP)

        val canvas = Canvas(bmp)
        canvas.drawBitmap(bgd, 0f, 0f, null)
        canvas.drawBitmap(fg, 0f, 0f, paint)

        return bmp
    }

    /**
     * 合并
     *
     * @param bgd 后景Bitmap
     * @param fg  前景Bitmap
     * @return 合成后Bitmap
     */
    fun combineImagesToSameSize(bgd: Bitmap, fg: Bitmap): Bitmap {
        var bgd = bgd
        var fg = fg
        val bmp: Bitmap

        val width = if (bgd.width < fg.width)
            bgd.width
        else
            fg
                    .width
        val height = if (bgd.height < fg.height)
            bgd.height
        else
            fg
                    .height

        if (fg.width != width && fg.height != height) {
            fg = zoom(fg, width, height)
        }
        if (bgd.width != width && bgd.height != height) {
            bgd = zoom(bgd, width, height)
        }

        bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(Mode.SRC_ATOP)

        val canvas = Canvas(bmp)
        canvas.drawBitmap(bgd, 0f, 0f, null)
        canvas.drawBitmap(fg, 0f, 0f, paint)

        return bmp
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap 源Bitmap
     * @param w      宽
     * @param h      高
     * @return 目标Bitmap
     */
    fun zoom(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleWidht = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        matrix.postScale(scaleWidht, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true)
    }

    /**
     * 获得圆角的Bitmap
     *
     * @param bitmap  源Bitmap
     * @param roundPx 圆角大小
     * @return 期望Bitmap
     */
    fun getRoundedCornerBitmap(bitmap: Bitmap, roundPx: Float): Bitmap {

        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    /**
     * 获得带倒影的Bitmap
     *
     * @param bitmap 源Bitmap
     * @return 带倒影的Bitmap
     */
    fun createReflectionBitmap(bitmap: Bitmap): Bitmap {
        val reflectionGap = 4
        val width = bitmap.width
        val height = bitmap.height

        val matrix = Matrix()
        matrix.preScale(1f, -1f)

        val reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false)

        val bitmapWithReflection = Bitmap.createBitmap(width,
                height + height / 2, Config.ARGB_8888)

        val canvas = Canvas(bitmapWithReflection)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val deafalutPaint = Paint()
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), (height + reflectionGap).toFloat(), deafalutPaint)

        canvas.drawBitmap(reflectionImage, 0f, (height + reflectionGap).toFloat(), null)

        val paint = Paint()
        val shader = LinearGradient(0f, bitmap.height.toFloat(), 0f,
                (bitmapWithReflection.height + reflectionGap).toFloat(), 0x70ffffff,
                0x00ffffff, TileMode.CLAMP)
        paint.shader = shader
        // Set the Transfer mode to be porter duff and destination in
        paint.xfermode = PorterDuffXfermode(Mode.DST_IN)
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), (bitmapWithReflection.height + reflectionGap).toFloat(), paint)

        return bitmapWithReflection
    }

    /**
     * 压缩图片大小
     *
     * @param image 源Bitmap
     * @return 压缩后的Bitmap
     */
    fun compressImage(image: Bitmap): Bitmap? {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset()// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10// 每次都减少10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())// 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
    }

    /**
     * 图片压缩-质量压缩
     *
     * @param filePath 源图片路径
     * @return 压缩后的路径
     */

    fun compressImage(filePath: String): String {

        //原文件
        val oldFile = File(filePath)

        //压缩文件路径 照片路径/
        val targetPath = oldFile.path
        val quality = 50//压缩比例0-100
        var bm = getSmallBitmap(filePath)//获取一定尺寸的图片
        val degree = getRotateAngle(filePath)//获取相片拍摄角度

        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bm = rotate(bm, degree)
        }
        val outputFile = File(targetPath)
        try {
            if (!outputFile.exists()) {
                outputFile.parentFile.mkdirs()
                //outputFile.createNewFile();
            } else {
                outputFile.delete()
            }
            val out = FileOutputStream(outputFile)
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return filePath
        }

        return outputFile.path
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    fun getSmallBitmap(filePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options)
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize1(options, 480, 800)
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    fun calculateInSampleSize1(options: BitmapFactory.Options,
                               reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    /**
     * 将彩色图转换为灰度图
     *
     * @param img 源Bitmap
     * @return 返回转换好的位图
     */
    fun convertGreyImg(img: Bitmap): Bitmap {
        val width = img.width // 获取位图的宽
        val height = img.height // 获取位图的高

        val pixels = IntArray(width * height) // 通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height)
        val alpha = 0xFF shl 24
        for (i in 0 until height) {
            for (j in 0 until width) {
                var grey = pixels[width * i + j]

                val red = grey and 0x00FF0000 shr 16
                val green = grey and 0x0000FF00 shr 8
                val blue = grey and 0x000000FF

                grey = (red.toFloat() * 0.3 + green.toFloat() * 0.59 + blue.toFloat() * 0.11).toInt()
                grey = alpha or (grey shl 16) or (grey shl 8) or grey
                pixels[width * i + j] = grey
            }
        }
        val result = Bitmap.createBitmap(width, height, Config.RGB_565)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    /**
     * 获得圆形的Bitmap
     *
     * @param bitmap 传入Bitmap对象
     * @return 圆形Bitmap
     */
    fun getRoundBitmap(bitmap: Bitmap): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val roundPx: Float
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dst_left: Float
        val dst_top: Float
        val dst_right: Float
        val dst_bottom: Float
        if (width <= height) {
            roundPx = (width / 2).toFloat()
            top = 0f
            bottom = width.toFloat()
            left = 0f
            right = width.toFloat()
            height = width
            dst_left = 0f
            dst_top = 0f
            dst_right = width.toFloat()
            dst_bottom = width.toFloat()
        } else {
            roundPx = (height / 2).toFloat()
            val clip = ((width - height) / 2).toFloat()
            left = clip
            right = width - clip
            top = 0f
            bottom = height.toFloat()
            width = height
            dst_left = 0f
            dst_top = 0f
            dst_right = height.toFloat()
            dst_bottom = height.toFloat()
        }

        val output = Bitmap.createBitmap(width, height, Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(),
                bottom.toInt())
        val dst = Rect(dst_left.toInt(), dst_top.toInt(),
                dst_right.toInt(), dst_bottom.toInt())
        val rectF = RectF(dst)

        paint.isAntiAlias = true

        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
        canvas.drawBitmap(bitmap, src, dst, paint)
        return output
    }

    /**
     * Returns a Bitmap representing the thumbnail of the specified Bitmap. The
     * size of the thumbnail is defined by the dimension
     * android.R.dimen.launcher_application_icon_size.
     *
     *
     * This method is not thread-safe and should be invoked on the UI thread
     * only.
     *
     * @param bitmap  The bitmap to get a thumbnail of.
     * @param context The application's context.
     * @return A thumbnail for the specified bitmap or the bitmap itself if the
     * thumbnail could not be created.
     */
    fun createThumbnailBitmap(bitmap: Bitmap, context: Context): Bitmap {
        var sIconWidth = -1
        var sIconHeight = -1
        val resources = context.resources
        sIconHeight = resources
                .getDimension(android.R.dimen.app_icon_size).toInt()
        sIconWidth = sIconHeight

        val sPaint = Paint()
        val sBounds = Rect()
        val sOldBounds = Rect()
        val sCanvas = Canvas()

        var width = sIconWidth
        var height = sIconHeight

        sCanvas.drawFilter = PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                Paint.FILTER_BITMAP_FLAG)

        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height

        if (width > 0 && height > 0) {
            if (width < bitmapWidth || height < bitmapHeight) {
                val ratio = bitmapWidth.toFloat() / bitmapHeight

                if (bitmapWidth > bitmapHeight) {
                    height = (width / ratio).toInt()
                } else if (bitmapHeight > bitmapWidth) {
                    width = (height * ratio).toInt()
                }

                val c = if (width == sIconWidth && height == sIconHeight)
                    bitmap
                            .config
                else
                    Config.ARGB_8888
                val thumb = Bitmap.createBitmap(sIconWidth,
                        sIconHeight, c)
                sCanvas.setBitmap(thumb)
                sPaint.isDither = false
                sPaint.isFilterBitmap = true
                sBounds.set((sIconWidth - width) / 2,
                        (sIconHeight - height) / 2, width, height)
                sOldBounds.set(0, 0, bitmapWidth, bitmapHeight)
                sCanvas.drawBitmap(bitmap, sOldBounds, sBounds, sPaint)
                return thumb
            } else if (bitmapWidth < width || bitmapHeight < height) {
                val c = Config.ARGB_8888
                val thumb = Bitmap.createBitmap(sIconWidth,
                        sIconHeight, c)
                sCanvas.setBitmap(thumb)
                sPaint.isDither = false
                sPaint.isFilterBitmap = true
                sCanvas.drawBitmap(bitmap, ((sIconWidth - bitmapWidth) / 2).toFloat(),
                        ((sIconHeight - bitmapHeight) / 2).toFloat(), sPaint)
                return thumb
            }
        }

        return bitmap
    }

    /**
     * 生成水印图片 水印在右下角
     *
     * @param src       the bitmap object you want proecss
     * @param watermark the water mark above the src
     * @return return a bitmap object ,if paramter's length is 0,return null
     */
    fun createWatermarkBitmap(src: Bitmap?, watermark: Bitmap): Bitmap? {
        if (src == null) {
            return null
        }

        val w = src.width
        val h = src.height
        val ww = watermark.width
        val wh = watermark.height
        // create the new blank bitmap
        val newb = Bitmap.createBitmap(w, h, Config.ARGB_8888)// 创建一个新的和SRC长度宽度一样的位图
        val cv = Canvas(newb)
        // draw src into
        cv.drawBitmap(src, 0f, 0f, null)// 在 0，0坐标开始画入src
        // draw watermark into
        cv.drawBitmap(watermark, (w - ww + 5).toFloat(), (h - wh + 5).toFloat(), null)// 在src的右下角画入水印
        // save all clip
        cv.save()// 保存
        // store
        cv.restore()// 存储
        return newb
    }

    /**
     * 重新编码Bitmap
     *
     * @param src     需要重新编码的Bitmap
     * @param format  编码后的格式（目前只支持png和jpeg这两种格式）
     * @param quality 重新生成后的bitmap的质量
     * @return 返回重新生成后的bitmap
     */
    fun codec(src: Bitmap, format: Bitmap.CompressFormat,
              quality: Int): Bitmap {
        val os = ByteArrayOutputStream()
        src.compress(format, quality, os)

        val array = os.toByteArray()
        return BitmapFactory.decodeByteArray(array, 0, array.size)
    }

    /**
     * 图片压缩处理（使用compress的方法）
     *
     *
     * <br></br>
     * **说明** 如果bitmap本身的大小小于maxSize，则不作处理
     *
     * @param bitmap  要压缩的图片
     * @param maxSize 压缩后的大小，单位kb
     */
    fun compress(bitmap: Bitmap, maxSize: Double) {
        var bitmap = bitmap
        // 将bitmap放至数组中，意在获得bitmap的大小（与实际读取的原文件要大）
        val baos = ByteArrayOutputStream()
        // 格式、质量、输出流
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos)
        val b = baos.toByteArray()
        // 将字节换成KB
        val mid = (b.size / 1024).toDouble()
        // 获取bitmap大小 是允许最大大小的多少倍
        val i = mid / maxSize
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (i > 1) {
            // 缩放图片 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            // （保持宽高不变，缩放后也达到了最大占用空间的大小）
            bitmap = scale(bitmap, bitmap.width / Math.sqrt(i),
                    bitmap.height / Math.sqrt(i))
        }
    }

    /**
     * 图片的缩放方法
     *
     * @param src       ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     */
    fun scale(src: Bitmap, newWidth: Double, newHeight: Double): Bitmap {
        // 记录src的宽高
        val width = src.width.toFloat()
        val height = src.height.toFloat()
        // 创建一个matrix容器
        val matrix = Matrix()
        // 计算缩放比例
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 开始缩放
        matrix.postScale(scaleWidth, scaleHeight)
        // 创建缩放后的图片
        return Bitmap.createBitmap(src, 0, 0, width.toInt(), height.toInt(),
                matrix, true)
    }

    /**
     * 图片的缩放方法
     *
     * @param src         ：源图片资源
     * @param scaleMatrix ：缩放规则
     */
    fun scale(src: Bitmap, scaleMatrix: Matrix): Bitmap {
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height,
                scaleMatrix, true)
    }

    /**
     * 图片的缩放方法
     *
     * @param src    ：源图片资源
     * @param scaleX ：横向缩放比例
     * @param scaleY ：纵向缩放比例
     */
    fun scale(src: Bitmap, scaleX: Float, scaleY: Float): Bitmap {
        val matrix = Matrix()
        matrix.postScale(scaleX, scaleY)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height,
                matrix, true)
    }

    /**
     * 图片的缩放方法
     *
     * @param src   ：源图片资源
     * @param scale ：缩放比例
     */
    fun scale(src: Bitmap, scale: Float): Bitmap {
        return scale(src, scale, scale)
    }

    /**
     * 旋转图片
     *
     * @param angle  旋转角度
     * @param bitmap 要旋转的图片
     * @return 旋转后的图片
     */
    fun rotate(bitmap: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, true)
    }

    /**
     * 水平翻转图片
     *
     * @param bitmap 原图
     * @return 水平翻转后的图片
     */
    fun reverseByHorizontal(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1f, 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, false)
    }

    /**
     * 垂直翻转图片
     *
     * @param bitmap 原图
     * @return 垂直翻转后的图片
     */
    fun reverseByVertical(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(1f, -1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, false)
    }

    /**
     * 更改图片色系，变亮或变暗
     *
     * @param delta 图片的亮暗程度值，越小图片会越亮，取值范围(0,24)
     * @return
     */
    fun adjustTone(src: Bitmap, delta: Int): Bitmap? {
        if (delta >= 24 || delta <= 0) {
            return null
        }
        // 设置高斯矩阵
        val gauss = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)
        val width = src.width
        val height = src.height
        val bitmap = Bitmap.createBitmap(width, height,
                Config.RGB_565)

        var pixR = 0
        var pixG = 0
        var pixB = 0
        var pixColor = 0
        var newR = 0
        var newG = 0
        var newB = 0
        var idx = 0
        val pixels = IntArray(width * height)

        src.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        pixColor = pixels[(i + m) * width + k + n]
                        pixR = Color.red(pixColor)
                        pixG = Color.green(pixColor)
                        pixB = Color.blue(pixColor)

                        newR += pixR * gauss[idx]
                        newG += pixG * gauss[idx]
                        newB += pixB * gauss[idx]
                        idx++
                    }
                }
                newR /= delta
                newG /= delta
                newB /= delta
                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))
                pixels[i * width + k] = Color.argb(255, newR, newG, newB)
                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * 将彩色图转换为黑白图
     *
     * @param bmp 位图
     * @return 返回转换好的位图
     */
    fun convertToBlackWhite(bmp: Bitmap): Bitmap {
        val width = bmp.width
        val height = bmp.height
        val pixels = IntArray(width * height)
        bmp.getPixels(pixels, 0, width, 0, 0, width, height)

        val alpha = 0xFF shl 24 // 默认将bitmap当成24色图片
        for (i in 0 until height) {
            for (j in 0 until width) {
                var grey = pixels[width * i + j]

                val red = grey and 0x00FF0000 shr 16
                val green = grey and 0x0000FF00 shr 8
                val blue = grey and 0x000000FF

                grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
                grey = alpha or (grey shl 16) or (grey shl 8) or grey
                pixels[width * i + j] = grey
            }
        }
        val newBmp = Bitmap.createBitmap(width, height, Config.RGB_565)
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBmp
    }

    /**
     * 读取图片属性：图片被旋转的角度
     *
     * @param path 图片绝对路径
     * @return 旋转的角度
     */
    fun getRotateAngle(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }


    /**
     * 饱和度处理
     *
     * @param bitmap          原图
     * @param saturationValue 新的饱和度值
     * @return 改变了饱和度值之后的图片
     */
    fun saturation(bitmap: Bitmap, saturationValue: Int): Bitmap {
        // 计算出符合要求的饱和度值
        val newSaturationValue = saturationValue * 1.0f / 127
        // 创建一个颜色矩阵
        val saturationColorMatrix = ColorMatrix()
        // 设置饱和度值
        saturationColorMatrix.setSaturation(newSaturationValue)
        // 创建一个画笔并设置其颜色过滤器
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(saturationColorMatrix)
        // 创建一个新的图片并创建画布
        val newBitmap = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        // 将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return newBitmap
    }

    /**
     * 亮度处理
     *
     * @param bitmap   原图
     * @param lumValue 新的亮度值
     * @return 改变了亮度值之后的图片
     */
    fun lum(bitmap: Bitmap, lumValue: Int): Bitmap {
        // 计算出符合要求的亮度值
        val newlumValue = lumValue * 1.0f / 127
        // 创建一个颜色矩阵
        val lumColorMatrix = ColorMatrix()
        // 设置亮度值
        lumColorMatrix.setScale(newlumValue, newlumValue, newlumValue, 1f)
        // 创建一个画笔并设置其颜色过滤器
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(lumColorMatrix)
        // 创建一个新的图片并创建画布
        val newBitmap = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        // 将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return newBitmap
    }

    /**
     * 色相处理
     *
     * @param bitmap   原图
     * @param hueValue 新的色相值
     * @return 改变了色相值之后的图片
     */
    fun hue(bitmap: Bitmap, hueValue: Int): Bitmap {
        // 计算出符合要求的色相值
        val newHueValue = (hueValue - 127) * 1.0f / 127 * 180
        // 创建一个颜色矩阵
        val hueColorMatrix = ColorMatrix()
        // 控制让红色区在色轮上旋转的角度
        hueColorMatrix.setRotate(0, newHueValue)
        // 控制让绿红色区在色轮上旋转的角度
        hueColorMatrix.setRotate(1, newHueValue)
        // 控制让蓝色区在色轮上旋转的角度
        hueColorMatrix.setRotate(2, newHueValue)
        // 创建一个画笔并设置其颜色过滤器
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(hueColorMatrix)
        // 创建一个新的图片并创建画布
        val newBitmap = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        // 将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return newBitmap
    }

    /**
     * 亮度、色相、饱和度处理
     *
     * @param bitmap          原图
     * @param lumValue        亮度值
     * @param hueValue        色相值
     * @param saturationValue 饱和度值
     * @return 亮度、色相、饱和度处理后的图片
     */
    fun lumAndHueAndSaturation(bitmap: Bitmap, lumValue: Int,
                               hueValue: Int, saturationValue: Int): Bitmap {
        // 计算出符合要求的饱和度值
        val newSaturationValue = saturationValue * 1.0f / 127
        // 计算出符合要求的亮度值
        val newlumValue = lumValue * 1.0f / 127
        // 计算出符合要求的色相值
        val newHueValue = (hueValue - 127) * 1.0f / 127 * 180

        // 创建一个颜色矩阵并设置其饱和度
        val colorMatrix = ColorMatrix()

        // 设置饱和度值
        colorMatrix.setSaturation(newSaturationValue)
        // 设置亮度值
        colorMatrix.setScale(newlumValue, newlumValue, newlumValue, 1f)
        // 控制让红色区在色轮上旋转的角度
        colorMatrix.setRotate(0, newHueValue)
        // 控制让绿红色区在色轮上旋转的角度
        colorMatrix.setRotate(1, newHueValue)
        // 控制让蓝色区在色轮上旋转的角度
        colorMatrix.setRotate(2, newHueValue)

        // 创建一个画笔并设置其颜色过滤器
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        // 创建一个新的图片并创建画布
        val newBitmap = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        // 将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return newBitmap
    }

    /**
     * 怀旧效果处理
     *
     * @param bitmap 原图
     * @return 怀旧效果处理后的图片
     */
    fun nostalgic(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(width, height,
                Config.RGB_565)
        var pixColor = 0
        var pixR = 0
        var pixG = 0
        var pixB = 0
        var newR = 0
        var newG = 0
        var newB = 0
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in 0 until height) {
            for (k in 0 until width) {
                pixColor = pixels[width * i + k]
                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)
                newR = (0.393 * pixR + 0.769 * pixG + 0.189 * pixB).toInt()
                newG = (0.349 * pixR + 0.686 * pixG + 0.168 * pixB).toInt()
                newB = (0.272 * pixR + 0.534 * pixG + 0.131 * pixB).toInt()
                val newColor = Color.argb(255, if (newR > 255) 255 else newR,
                        if (newG > 255) 255 else newG, if (newB > 255) 255 else newB)
                pixels[width * i + k] = newColor
            }
        }
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 柔化效果处理
     *
     * @param bitmap 原图
     * @return 柔化效果处理后的图片
     */
    fun soften(bitmap: Bitmap): Bitmap {
        // 高斯矩阵
        val gauss = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)

        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(width, height,
                Config.RGB_565)

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        val delta = 16 // 值越小图片会越亮，越大则越暗

        var idx = 0
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        pixColor = pixels[(i + m) * width + k + n]
                        pixR = Color.red(pixColor)
                        pixG = Color.green(pixColor)
                        pixB = Color.blue(pixColor)

                        newR = newR + pixR * gauss[idx]
                        newG = newG + pixG * gauss[idx]
                        newB = newB + pixB * gauss[idx]
                        idx++
                    }
                }

                newR /= delta
                newG /= delta
                newB /= delta

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                pixels[i * width + k] = Color.argb(255, newR, newG, newB)

                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 光照效果处理
     *
     * @param bitmap  原图
     * @param centerX 光源在X轴的位置
     * @param centerY 光源在Y轴的位置
     * @return 光照效果处理后的图片
     */
    fun sunshine(bitmap: Bitmap, centerX: Int, centerY: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(width, height,
                Config.RGB_565)

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0
        val radius = Math.min(centerX, centerY)

        val strength = 150f // 光照强度 100~150
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var pos = 0
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                pos = i * width + k
                pixColor = pixels[pos]

                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)

                newR = pixR
                newG = pixG
                newB = pixB

                // 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
                val distance = (Math.pow((centerY - i).toDouble(), 2.0) + Math.pow(
                        (centerX - k).toDouble(), 2.0)).toInt()
                if (distance < radius * radius) {
                    // 按照距离大小计算增加的光照值
                    val result = (strength * (1.0 - Math.sqrt(distance.toDouble()) / radius)).toInt()
                    newR = pixR + result
                    newG = pixG + result
                    newB = pixB + result
                }

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                pixels[pos] = Color.argb(255, newR, newG, newB)
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 底片效果处理
     *
     * @param bitmap 原图
     * @return 底片效果处理后的图片
     */
    fun film(bitmap: Bitmap): Bitmap {
        // RGBA的最大值
        val MAX_VALUE = 255
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(width, height,
                Config.RGB_565)

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var pos = 0
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                pos = i * width + k
                pixColor = pixels[pos]

                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)

                newR = MAX_VALUE - pixR
                newG = MAX_VALUE - pixG
                newB = MAX_VALUE - pixB

                newR = Math.min(MAX_VALUE, Math.max(0, newR))
                newG = Math.min(MAX_VALUE, Math.max(0, newG))
                newB = Math.min(MAX_VALUE, Math.max(0, newB))

                pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB)
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 锐化效果处理
     *
     * @param bitmap 原图
     * @return 锐化效果处理后的图片
     */
    fun sharpen(bitmap: Bitmap): Bitmap {
        // 拉普拉斯矩阵
        val laplacian = intArrayOf(-1, -1, -1, -1, 9, -1, -1, -1, -1)

        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(width, height,
                Config.RGB_565)

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        var idx = 0
        val alpha = 0.3f
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        pixColor = pixels[(i + n) * width + k + m]
                        pixR = Color.red(pixColor)
                        pixG = Color.green(pixColor)
                        pixB = Color.blue(pixColor)

                        newR = newR + (pixR.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        newG = newG + (pixG.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        newB = newB + (pixB.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        idx++
                    }
                }

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                pixels[i * width + k] = Color.argb(255, newR, newG, newB)
                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 浮雕效果处理
     *
     * @param bitmap 原图
     * @return 浮雕效果处理后的图片
     */
    fun emboss(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(width, height,
                Config.RGB_565)

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var pos = 0
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                pos = i * width + k
                pixColor = pixels[pos]

                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)

                pixColor = pixels[pos + 1]
                newR = Color.red(pixColor) - pixR + 127
                newG = Color.green(pixColor) - pixG + 127
                newB = Color.blue(pixColor) - pixB + 127

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                pixels[pos] = Color.argb(255, newR, newG, newB)
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 将YUV格式的图片的源数据从横屏模式转为竖屏模式，注意：将源图片的宽高互换一下就是新图片的宽高
     *
     * @param sourceData YUV格式的图片的源数据
     * @param width      源图片的宽
     * @param height     源图片的高
     * @return
     */
    fun yuvLandscapeToPortrait(sourceData: ByteArray,
                               width: Int, height: Int): ByteArray {
        val rotatedData = ByteArray(sourceData.size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                rotatedData[x * height + height - y - 1] = sourceData[x + y * width]
            }
        }
        return rotatedData
    }
}
