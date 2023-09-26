package com.myl.mediacodedemo.utils

object TextureRotationUtils {

    const val CoordsPerVertex = 2
    val CubeVertices = floatArrayOf(
        -1.0f, -1.0f,  // 0 bottom left
        1.0f, -1.0f,  // 1 bottom right
        -1.0f, 1.0f,  // 2 top left
        1.0f, 1.0f
    )
    val TextureVertices = floatArrayOf(
        0.0f, 0.0f,  // 0 left bottom
        1.0f, 0.0f,  // 1 right bottom
        0.0f, 1.0f,  // 2 left top
        1.0f, 1.0f // 3 right top
    )

    // x轴反过来
    val TextureVertices_flipx = floatArrayOf(
        1.0f, 0.0f,  // 0 right bottom
        0.0f, 0.0f,  // 1 left  bottom
        1.0f, 1.0f,  // 2 right top
        0.0f, 1.0f // 3 left  top
    )
    private val TextureVertices_90 = floatArrayOf(
        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f
    )
    private val TextureVertices_180 = floatArrayOf(
        1.0f, 1.0f,  // right top
        0.0f, 1.0f,  // left top
        1.0f, 0.0f,  // right bottom
        0.0f, 0.0f
    )
    private val TextureVertices_270 = floatArrayOf(
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    /**
     * 索引，glDrawElements使用
     */
    val Indices = shortArrayOf(
        0, 1, 2,
        2, 1, 3
    )

    /**
     * 获取旋转后的Buffer
     * @param rotation
     * @param flipHorizontal
     * @param flipVertical
     * @return
     */
    fun getRotation(
        rotation: Rotation?, flipHorizontal: Boolean,
        flipVertical: Boolean
    ): FloatArray {
        var rotatedTex: FloatArray
        rotatedTex = when (rotation) {
            Rotation.ROTATION_90 -> TextureVertices_90
            Rotation.ROTATION_180 -> TextureVertices_180
            Rotation.ROTATION_270 -> TextureVertices_270
            Rotation.NORMAL -> TextureVertices
            else -> TextureVertices
        }
        // 左右翻转
        if (flipHorizontal) {
            rotatedTex = floatArrayOf(
                flip(rotatedTex[0]), rotatedTex[1],
                flip(rotatedTex[2]), rotatedTex[3],
                flip(rotatedTex[4]), rotatedTex[5],
                flip(rotatedTex[6]), rotatedTex[7]
            )
        }
        // 上下翻转
        if (flipVertical) {
            rotatedTex = floatArrayOf(
                rotatedTex[0], flip(rotatedTex[1]),
                rotatedTex[2], flip(rotatedTex[3]),
                rotatedTex[4], flip(rotatedTex[5]),
                rotatedTex[6], flip(rotatedTex[7])
            )
        }
        return rotatedTex
    }

    /**
     * 翻转
     * @param i
     * @return
     */
    private fun flip(i: Float): Float {
        return if (i == 0.0f) {
            1.0f
        } else 0.0f
    }
}