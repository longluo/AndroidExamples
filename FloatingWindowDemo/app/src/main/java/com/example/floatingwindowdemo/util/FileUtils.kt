package com.example.floatingwindowdemo.util

import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import java.io.File


class FileUtils {

    //设置一个通用文件夹路径
    private val appDirName = "FloatWindowDemo"

    /**
     * 检查文件目录是否存在，不存在则创建目录
     * @param dirPath:文件目录
     * @return
     */
    fun checkFileDirExist(dirPath: String): Boolean {
        try {
            var file = File(dirPath)
            if (file.exists()) {
                return true
            } else {
                file.mkdir()
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 判断文件存不存在
     * @param filePath:带文件路径的文件名
     * @return true存在，false不存在
     */
    fun isFileExist(filePath: String): Boolean {
        if (filePath == "")
            return false
        return try {
            var file: File = File(filePath)
            file.exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取当前项目文件夹路径
     */
    fun getAppDir(): String? {
        var stringBuilder = StringBuilder()
        var sdCardPath: String? = getSDCardPath() ?: return null
        stringBuilder.append(sdCardPath).append(File.separator).append(appDirName)
                .append(File.separator)
        return stringBuilder.toString()
    }

    /**
     * 查询文件可不可读
     * @param fileName:带路径的文件名称
     * @return :需要判断文件存不存在
     */
    fun fileCanRead(fileName: String?): Boolean {
        if (fileName == null)
            return false
        var file = File(fileName)
        return isFileExist(fileName) && file.canRead()
    }

    /**
     * 获取文件外部存储的路径
     * @return
     */
    fun getSDCardPath(): String? {
        return try {
            return Environment.getExternalStorageDirectory().absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 删除文件夹下所有文件及文件夹
     *
     * @param dirFile
     * @return
     */
    fun deleteDirs(dirFile: File?): Boolean {
        return deleteDirs("", dirFile)
    }

    /**
     * 删除文件夹下所有文件及文件夹，保留根目录
     */
    fun deleteDirs(rootDir: String, dirFile: File?): Boolean {
        return deleteDirs(rootDir, dirFile, "")
    }


    /**
     * 删除文件夹下所有文件及文件夹，保留根目录
     *
     * @param rootDir
     * @param dirFile
     * @return
     */
    fun deleteDirs(rootDir: String, dirFile: File?, exceptPath: String?): Boolean {
        return try {
            if (dirFile != null && dirFile.exists() && dirFile.isDirectory) {
                if (!TextUtils.isEmpty(exceptPath) && dirFile.path.contains(exceptPath!!)) {
                    return true
                }
                if (dirFile.listFiles() == null)
                    return true
                for (f in dirFile.listFiles()) {
                    if (f.isFile) {
                        f.delete()
                    } else if (f.isDirectory) {
                        deleteDirs(rootDir, f, exceptPath)
                    }
                }
                if (rootDir != dirFile.path) {
                    return dirFile.delete()
                }
            }
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取当前外存的剩余空间
     */
    private fun getAvailSD(): Long {
        try {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong() // 得到可用区块
            //有多少个
            return blockSize * availableBlocks
        } catch (e: java.lang.Exception) {
        }
        return -1
    }

    /**
     * 获取当前内部存储的剩余空间
     */
    private fun getAvailDictionary(): Long {
        try {
            val path = Environment.getRootDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong() // 得到可用区块
            //有多少个
            return blockSize * availableBlocks
        } catch (e: java.lang.Exception) {
        }
        return -1
    }

    /**
     * 获取外存的总大小
     */
    private fun getALLSd(): Long {
        try {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.blockCount.toLong()
            return blockSize * availableBlocks
        } catch (e: java.lang.Exception) {
        }
        return -1
    }

    /**
     * 获取内部存储的总大小
     */
    private fun getALLDictionary(): Long {
        try {
            val path = Environment.getRootDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.blockCount.toLong() // 总区块
            //有多少个
            return blockSize * availableBlocks
        } catch (e: java.lang.Exception) {
        }
        return -1
    }

    /**
     * 判断指定的文件或文件夹删除是否成功
     * @param FileName 文件或文件夹的路径
     * @return true or false 成功返回true，失败返回false
     */
    fun deleteAnyone(FileName: String): Boolean {
        val file = File(FileName) //根据指定的文件名创建File对象
        return if (!file.exists()) {  //要删除的文件不存在
            println("文件" + FileName + "不存在，删除失败！")
            false
        } else { //要删除的文件存在
            if (file.isFile) { //如果目标文件是文件
                deleteFile(FileName)
            } else {  //如果目标文件是目录
                deleteDir(FileName)
            }
        }
    }


    /**
     * 判断指定的文件删除是否成功
     * @param FileName 文件路径
     * @return true or false 成功返回true，失败返回false
     */
    fun deleteFile(fileName: String): Boolean {
        val file = File(fileName) //根据指定的文件名创建File对象
        return if (file.exists() && file.isFile) { //要删除的文件存在且是文件
            if (file.delete()) {
                println("文件" + fileName + "删除成功！")
                true
            } else {
                println("文件" + fileName + "删除失败！")
                false
            }
        } else {
            println("文件" + fileName + "不存在，删除失败！")
            false
        }
    }


    /**
     * 删除指定的目录以及目录下的所有子文件
     * @param dirName is 目录路径
     * @return true or false 成功返回true，失败返回false
     */
    fun deleteDir(dirName: String): Boolean {
        var dirName = dirName
        if (dirName.endsWith(File.separator)) //dirName不以分隔符结尾则自动添加分隔符
            dirName += File.separator
        val file = File(dirName) //根据指定的文件名创建File对象
        if (!file.exists() || !file.isDirectory) { //目录不存在或者
            println("目录删除失败" + dirName + "目录不存在！")
            return false
        }
        val fileArrays = file.listFiles() //列出源文件下所有文件，包括子目录
        for (i in fileArrays.indices) { //将源文件下的所有文件逐个删除
            deleteAnyone(fileArrays[i].absolutePath)
        }
        if (file.delete()) //删除当前目录
            println("目录" + dirName + "删除成功！")
        return true
    }

    companion object {
        val instance = FileUtils()
    }
}