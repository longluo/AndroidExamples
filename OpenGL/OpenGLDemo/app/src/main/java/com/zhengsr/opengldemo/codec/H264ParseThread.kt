package com.zhengsr.opengldemo.codec

import java.io.InputStream

/**
 * @author by zhengshaorui 2022/12/28
 * describe：H264 帧解析类
 */
class H264ParseThread(val inputStream: InputStream, val listener: IFrameListener) : Thread() {
    companion object {
        private const val TAG = "H264Parse"

        //一般H264帧大小不超过200k,如果解码失败可以尝试增大这个值
        private const val FRAME_MAX_LEN = 300 * 1024
        private const val P_FRAME = 0x01
        private const val I_FRAME = 0x05
        private const val SPS = 0x07
        private const val PPS = 0x08
    }

    private var isFinish = false

    public interface IFrameListener {
        fun onLog(msg: String)
        fun onFrame(byteArray: ByteArray, offset: Int, count: Int)
    }

    override fun run() {
        super.run()

        try {
            isFinish = false
            val header = ByteArray(4)
            val formatLength = getHeaderFormatLength(header, inputStream)
            if (formatLength < 0) {
                listener.onLog("不符合H264文件规范: $formatLength")
                return
            }
            //帧数组
            val frame = ByteArray(FRAME_MAX_LEN)
            //每次读取的数据
            val readData = ByteArray(2 * 1024)
            //把头部信息给到 frame
            System.arraycopy(header, 0, frame, 0, header.size)

            //开始肯定是 sps，所以，帧的起始位置为0,由于前面读取了头部，偏移量为4
            var frameLen = 4

            while (!isFinish) {
                val readLen = inputStream.read(readData)
                if (readLen < 0) {
                    //文件末尾
                    listener.onLog("文件末尾，退出")
                    isFinish = true
                    return
                }
                if (frameLen + readLen > FRAME_MAX_LEN) {
                    //文件末尾
                    listener.onLog("文件末尾，大于预留数组，退出")
                    isFinish = true
                    return
                }
                //先把数据拷贝到帧数组
                System.arraycopy(readData, 0, frame, frameLen, readLen)
                //修改当前帧的大小
                frameLen += readLen
                //寻找第一帧
                var firstHeadIndex = findHeader(frame, 0, frameLen)
                while (firstHeadIndex >= 0) {
                    //找第二帧,从第一帧之后的间隔开始找
                    val secondFrameIndex =
                        findHeader(frame, firstHeadIndex + 100, frameLen)
                    if (secondFrameIndex > 0) {
                        //找到第二帧
                        listener.onFrame(frame, firstHeadIndex, secondFrameIndex - firstHeadIndex)
                        //把第二帧的数组数据，拷贝到前面，方便继续寻找下一帧
                        val temp = frame.copyOfRange(secondFrameIndex, frameLen)
                        System.arraycopy(temp, 0, frame, 0, temp.size)
                        //帧下表指向第二帧的数据
                        frameLen = temp.size

                        //继续寻找下一帧
                        firstHeadIndex = findHeader(frame, 0, frameLen)
                    } else {
                        //没有找到，继续循环去找
                        firstHeadIndex = -1
                    }


                }


            }

        } catch (e: Exception) {
            listener.onLog("read file fail: $e")
        }
    }

    fun release() {
        isFinish = true
    }

    private fun findHeader(data: ByteArray, offset: Int, count: Int): Int {
        for (i in offset until count) {
            if (isFrameHeader(data, i)) {
                return i
            }
        }
        return -1

    }

    private fun isFrameHeader(data: ByteArray, index: Int): Boolean {
        if (data.size < 5) {
            return false
        }
        val d1 = data[index].toInt() == 0
        val d2 = data[index + 1].toInt() == 0
        val isNaluHeader = d1 && d2
        if (isNaluHeader && data[index + 2].toInt() == 1 && isFrameHeadType(data[index + 3])){
            return true
        }else if (isNaluHeader && data[index + 2].toInt() == 0 && data[index + 3].toInt() == 1 && isFrameHeadType(data[index + 4])){
            return true
        }

        return false
    }

    /**
     * 解析的时候，找到I和P去解析即可
     * 为啥使用and这个会导致播放卡顿？有大佬可以解释一下吗
     */
    private fun isSpecialFrame(byte: Byte): Boolean {
        val type = byte.toInt() and 0x11
        return  type == P_FRAME || type == I_FRAME || type == SPS || type == PPS
    }

    /**
     * 65    --   I帧/IDR帧
     * 41/61 --   p帧
     * 67    --   sps
     * 68    --   pps
     *
     */
    fun isFrameHeadType(head: Byte): Boolean {
        // val type = byte.toInt() and 0x11
        return head == 0x65.toByte() || head == 0x61.toByte()
                || head == 0x41.toByte() || head == 0x67.toByte()
                || head == 0x68.toByte() || head == 0x06.toByte()
    }

    private fun getHeaderFormatLength(header: ByteArray, inputStream: InputStream): Int {
        //先读取头部4个字节，判断h264 是哪种格式
        if (header.size < 4) {
            return -1
        }
        inputStream.read(header)
        val h1 = header[0].toInt()
        val h2 = header[1].toInt()
        val h3 = header[2].toInt()
        val h4 = header[3].toInt()
        //码流格式类型00 00 01 或者 00 00 00 01
        return if (h1 == 0 && h2 == 0 && h3 == 1) {
            3
        } else if (h1 == 0 && h2 == 0 && h3 == 0 && h4 == 1) {
            4
        } else {
            //不符合H264文件规范
            -1
        }

    }
}