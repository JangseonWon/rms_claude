package com.gcgenome.lims.encrypt

import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
object SHA256 {
    // DEFAULT : JAVA = BIG_ENDIAN
    private const val ENDIAN = Common.BIG_ENDIAN
    private const val SHA256_DIGEST_BLOCKLEN = 64
    private const val SHA256_DIGEST_VALUELEN = 32
    private val SHA256_K = intArrayOf(
        0x428a2f98, 0x71374491, -0x4a3f0431, -0x164a245b, 0x3956c25b, 0x59f111f1,
        -0x6dc07d5c, -0x54e3a12b, -0x27f85568, 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, -0x7f214e02, -0x6423f959, -0x3e640e8c, -0x1b64963f, -0x1041b87a,
        0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        -0x67c1aeae, -0x57ce3993, -0x4ffcd838, -0x40a68039, -0x391ff40d, -0x2a586eb9,
        0x06ca6351, 0x14292967, 0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, -0x7e3d36d2, -0x6d8dd37b, -0x5d40175f, -0x57e599b5,
        -0x3db47490, -0x3893ae5d, -0x2e6d17e7, -0x2966f9dc, -0xbf1ca7b, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a,
        0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f, -0x7b3787ec, -0x7338fdf8,
        -0x6f410006, -0x5baf9315, -0x41065c09, -0x398e870e
    )

    private fun ROTL_ULONG(x: Int, n: Int): Int {
        return x shl n or Common.URShift(x, 32 - n)
    }

    private fun ROTR_ULONG(x: Int, n: Int): Int {
        return Common.URShift(x, n) or (x shl 32 - n)
    }

    private fun ENDIAN_REVERSE_ULONG(dwS: Int): Int {
        return ROTL_ULONG(dwS, 8) and 0x00ff00ff or (ROTL_ULONG(
            dwS, 24
        ) and -0xff0100)
    }

    private fun BIG_D2B(D: Int, B: ByteArray, B_offset: Int) {
        Common.int_to_byte_unit(B, B_offset, D, ENDIAN)
    }

    private fun RR(x: Int, n: Int): Int {
        return ROTR_ULONG(x, n)
    }

    private fun SS(x: Int, n: Int): Int {
        return Common.URShift(x, n)
    }

    private fun Ch(x: Int, y: Int, z: Int): Int {
        return x and y xor (x.inv() and z)
    }

    private fun Maj(x: Int, y: Int, z: Int): Int {
        return x and y xor (x and z) xor (y and z)
    }

    private fun Sigma0(x: Int): Int {
        return RR(x, 2) xor RR(x, 13) xor RR(x, 22)
    }

    private fun Sigma1(x: Int): Int {
        return RR(x, 6) xor RR(x, 11) xor RR(x, 25)
    }

    private fun RHO0(x: Int): Int {
        return RR(x, 7) xor RR(x, 18) xor SS(x, 3)
    }

    private fun RHO1(x: Int): Int {
        return RR(x, 17) xor RR(x, 19) xor SS(x, 10)
    }

    private const val abcdefgh_a = 0
    private const val abcdefgh_b = 1
    private const val abcdefgh_c = 2
    private const val abcdefgh_d = 3
    private const val abcdefgh_e = 4
    private const val abcdefgh_f = 5
    private const val abcdefgh_g = 6
    private const val abcdefgh_h = 7
    private fun FF(
        abcdefgh: IntArray,
        a: Int,
        b: Int,
        c: Int,
        d: Int,
        e: Int,
        f: Int,
        g: Int,
        h: Int,
        X: IntArray,
        j: Int
    ) {
        val T1: Long
        T1 = Common.intToUnsigned(abcdefgh[h]) + Common.intToUnsigned(
            Sigma1(
                abcdefgh[e]
            )
        ) + Common.intToUnsigned(
            Ch(
                abcdefgh[e],
                abcdefgh[f], abcdefgh[g]
            )
        ) + Common.intToUnsigned(SHA256_K[j]) + Common.intToUnsigned(
            X[j]
        )
        abcdefgh[d] += T1.toInt()
        abcdefgh[h] = (T1 + Common.intToUnsigned(
            Sigma0(
                abcdefgh[a]
            )
        ) + Common.intToUnsigned(
            Maj(
                abcdefgh[a],
                abcdefgh[b], abcdefgh[c]
            )
        )).toInt()
    }

    private fun GetData(x: ByteArray, x_offset: Int): Int {
        return Common.byte_to_int(x, x_offset, ENDIAN)
    }

    private fun SHA256_Transform(Message: ByteArray, ChainVar: IntArray) {
        val abcdefgh = IntArray(8)
        val X = IntArray(64)
        var j: Int
        j = 0
        while (j < 16) {
            X[j] = GetData(Message, j * 4)
            j++
        }
        j = 16
        while (j < 64) {
            X[j] = (Common.intToUnsigned(RHO1(X[j - 2])) + Common.intToUnsigned(
                X[j - 7]
            ) + Common.intToUnsigned(RHO0(X[j - 15])) + Common.intToUnsigned(
                X[j - 16]
            )).toInt()
            j++
        }
        abcdefgh[abcdefgh_a] = ChainVar[0]
        abcdefgh[abcdefgh_b] = ChainVar[1]
        abcdefgh[abcdefgh_c] = ChainVar[2]
        abcdefgh[abcdefgh_d] = ChainVar[3]
        abcdefgh[abcdefgh_e] = ChainVar[4]
        abcdefgh[abcdefgh_f] = ChainVar[5]
        abcdefgh[abcdefgh_g] = ChainVar[6]
        abcdefgh[abcdefgh_h] = ChainVar[7]
        j = 0
        while (j < 64) {
            FF(
                abcdefgh,
                abcdefgh_a,
                abcdefgh_b,
                abcdefgh_c,
                abcdefgh_d,
                abcdefgh_e,
                abcdefgh_f,
                abcdefgh_g,
                abcdefgh_h,
                X,
                j + 0
            )
            FF(
                abcdefgh,
                abcdefgh_h,
                abcdefgh_a,
                abcdefgh_b,
                abcdefgh_c,
                abcdefgh_d,
                abcdefgh_e,
                abcdefgh_f,
                abcdefgh_g,
                X,
                j + 1
            )
            FF(
                abcdefgh,
                abcdefgh_g,
                abcdefgh_h,
                abcdefgh_a,
                abcdefgh_b,
                abcdefgh_c,
                abcdefgh_d,
                abcdefgh_e,
                abcdefgh_f,
                X,
                j + 2
            )
            FF(
                abcdefgh,
                abcdefgh_f,
                abcdefgh_g,
                abcdefgh_h,
                abcdefgh_a,
                abcdefgh_b,
                abcdefgh_c,
                abcdefgh_d,
                abcdefgh_e,
                X,
                j + 3
            )
            FF(
                abcdefgh,
                abcdefgh_e,
                abcdefgh_f,
                abcdefgh_g,
                abcdefgh_h,
                abcdefgh_a,
                abcdefgh_b,
                abcdefgh_c,
                abcdefgh_d,
                X,
                j + 4
            )
            FF(
                abcdefgh,
                abcdefgh_d,
                abcdefgh_e,
                abcdefgh_f,
                abcdefgh_g,
                abcdefgh_h,
                abcdefgh_a,
                abcdefgh_b,
                abcdefgh_c,
                X,
                j + 5
            )
            FF(
                abcdefgh,
                abcdefgh_c,
                abcdefgh_d,
                abcdefgh_e,
                abcdefgh_f,
                abcdefgh_g,
                abcdefgh_h,
                abcdefgh_a,
                abcdefgh_b,
                X,
                j + 6
            )
            FF(
                abcdefgh,
                abcdefgh_b,
                abcdefgh_c,
                abcdefgh_d,
                abcdefgh_e,
                abcdefgh_f,
                abcdefgh_g,
                abcdefgh_h,
                abcdefgh_a,
                X,
                j + 7
            )
            j += 8
        }
        ChainVar[0] += abcdefgh[abcdefgh_a]
        ChainVar[1] += abcdefgh[abcdefgh_b]
        ChainVar[2] += abcdefgh[abcdefgh_c]
        ChainVar[3] += abcdefgh[abcdefgh_d]
        ChainVar[4] += abcdefgh[abcdefgh_e]
        ChainVar[5] += abcdefgh[abcdefgh_f]
        ChainVar[6] += abcdefgh[abcdefgh_g]
        ChainVar[7] += abcdefgh[abcdefgh_h]
    }

    private fun SHA256_Init(Info: SHA256_INFO) {
        Info.uChainVar[0] = 0x6a09e667
        Info.uChainVar[1] = -0x4498517b
        Info.uChainVar[2] = 0x3c6ef372
        Info.uChainVar[3] = -0x5ab00ac6
        Info.uChainVar[4] = 0x510e527f
        Info.uChainVar[5] = -0x64fa9774
        Info.uChainVar[6] = 0x1f83d9ab
        Info.uChainVar[7] = 0x5be0cd19
        Info.uLowLength = 0
        Info.uHighLength = Info.uLowLength
    }

    private fun SHA256_Process(Info: SHA256_INFO, pszMessage: ByteArray, uDataLen: Int) {
        var uDataLen = uDataLen
        var pszMessage_offset: Int
        if ((uDataLen shl 3).let { Info.uLowLength += it; Info.uLowLength } < 0) {
            Info.uHighLength++
        }
        Info.uHighLength += Common.URShift(uDataLen, 29)
        pszMessage_offset = 0
        while (uDataLen >= SHA256_DIGEST_BLOCKLEN) {
            Common.arraycopy_offset(Info.szBuffer, 0, pszMessage, pszMessage_offset, SHA256_DIGEST_BLOCKLEN)
            SHA256_Transform(Info.szBuffer, Info.uChainVar)
            pszMessage_offset += SHA256_DIGEST_BLOCKLEN
            uDataLen -= SHA256_DIGEST_BLOCKLEN
        }
        Common.arraycopy_offset(Info.szBuffer, 0, pszMessage, pszMessage_offset, uDataLen)
    }

    private fun SHA256_Close(Info: SHA256_INFO, pszDigest: ByteArray) {
        var i: Int
        var Index: Int
        Index = Common.URShift(Info.uLowLength, 3) % SHA256_DIGEST_BLOCKLEN
        Info.szBuffer[Index++] = 0x80.toByte()
        if (Index > SHA256_DIGEST_BLOCKLEN - 8) {
            Common.arrayinit_offset(Info.szBuffer, Index, 0.toByte(), SHA256_DIGEST_BLOCKLEN - Index)
            SHA256_Transform(Info.szBuffer, Info.uChainVar)
            Common.arrayinit(Info.szBuffer, 0.toByte(), SHA256_DIGEST_BLOCKLEN - 8)
        } else {
            Common.arrayinit_offset(Info.szBuffer, Index, 0.toByte(), SHA256_DIGEST_BLOCKLEN - Index - 8)
        }
        if (ENDIAN == Common.LITTLE_ENDIAN) {
            Info.uLowLength = ENDIAN_REVERSE_ULONG(Info.uLowLength)
            Info.uHighLength = ENDIAN_REVERSE_ULONG(Info.uHighLength)
        }
        Common.int_to_byte_unit(Info.szBuffer, (SHA256_DIGEST_BLOCKLEN / 4 - 2) * 4, Info.uHighLength, ENDIAN)
        Common.int_to_byte_unit(Info.szBuffer, (SHA256_DIGEST_BLOCKLEN / 4 - 1) * 4, Info.uLowLength, ENDIAN)
        SHA256_Transform(Info.szBuffer, Info.uChainVar)
        i = 0
        while (i < SHA256_DIGEST_VALUELEN) {
            BIG_D2B(Info.uChainVar[i / 4], pszDigest, i)
            i += 4
        }
    }

    private fun SHA256_Encrpyt(pszMessage: ByteArray, uPlainTextLen: Int, pszDigest: ByteArray) {
        val info = SHA256_INFO()
        SHA256_Init(info)
        SHA256_Process(info, pszMessage, uPlainTextLen)
        SHA256_Close(info, pszDigest)
    }

    fun convert(str: String): String {
        val bytes = str.toByteArray()
        val digest = ByteArray(32)
        SHA256_Encrpyt(bytes, bytes.size, digest)
        return BigInteger(1, digest).toString(16)
    }

    private class SHA256_INFO {
        val uChainVar = IntArray(SHA256_DIGEST_VALUELEN / 4)
        var uHighLength = 0
        var uLowLength = 0
        val szBuffer = ByteArray(SHA256_DIGEST_BLOCKLEN)
    }

    private object Common {
        const val BIG_ENDIAN = 0
        const val LITTLE_ENDIAN = 1
        fun arraycopy_offset(dst: ByteArray, dst_offset: Int, src: ByteArray, src_offset: Int, length: Int) {
            for (i in 0 until length) {
                dst[dst_offset + i] = src[src_offset + i]
            }
        }

        fun arrayinit(dst: ByteArray, value: Byte, length: Int) {
            for (i in 0 until length) {
                dst[i] = value
            }
        }

        fun arrayinit_offset(dst: ByteArray, dst_offset: Int, value: Byte, length: Int) {
            for (i in 0 until length) {
                dst[dst_offset + i] = value
            }
        }

        fun byte_to_int(src: ByteArray, src_offset: Int, ENDIAN: Int): Int {
            return if (ENDIAN == BIG_ENDIAN) {
                0x0ff and src[src_offset].toInt() shl 24 or (0x0ff and src[src_offset + 1].toInt() shl 16) or (0x0ff and src[src_offset + 2].toInt() shl 8) or (0x0ff and src[src_offset + 3].toInt())
            } else {
                0x0ff and src[src_offset].toInt() or (0x0ff and src[src_offset + 1].toInt() shl 8) or (0x0ff and src[src_offset + 2].toInt() shl 16) or (0x0ff and src[src_offset + 3].toInt() shl 24)
            }
        }

        fun int_to_byte_unit(dst: ByteArray, dst_offset: Int, src: Int, ENDIAN: Int) {
            if (ENDIAN == BIG_ENDIAN) {
                dst[dst_offset] = (src shr 24 and 0x0ff).toByte()
                dst[dst_offset + 1] = (src shr 16 and 0x0ff).toByte()
                dst[dst_offset + 2] = (src shr 8 and 0x0ff).toByte()
                dst[dst_offset + 3] = (src and 0x0ff).toByte()
            } else {
                dst[dst_offset] = (src and 0x0ff).toByte()
                dst[dst_offset + 1] = (src shr 8 and 0x0ff).toByte()
                dst[dst_offset + 2] = (src shr 16 and 0x0ff).toByte()
                dst[dst_offset + 3] = (src shr 24 and 0x0ff).toByte()
            }
        }

        fun URShift(x: Int, n: Int): Int {
            if (n == 0) return x
            if (n >= 32) return 0
            val v = x shr n
            val v_mask = (-0x80000000 shr n - 1).inv()
            return v and v_mask
        }

        private val INT_RANGE_MAX = Math.pow(2.0, 32.0).toLong()
        fun intToUnsigned(x: Int): Long {
            return if (x >= 0) x.toLong() else x + INT_RANGE_MAX
        }
    }
}
