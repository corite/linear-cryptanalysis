import kotlin.math.abs
import kotlin.random.Random

@ExperimentalUnsignedTypes
class SubstitutionPermutationNetwork {

    private fun subBlock(num:UShort):UShort{
        val array = ushortArrayOf("E".toUShort(16),4u,"D".toUShort(16),1u,2u,"F".toUShort(16),"B".toUShort(16),8u,3u,"A".toUShort(16),6u,"C".toUShort(16),5u,9u,0u,7u)
        return array[num.toInt()]
    }
    private fun revSubBlock(num:UShort):UShort{
        val array = ushortArrayOf("E".toUShort(16),3u,4u,8u,1u,"C".toUShort(16),"A".toUShort(16),"F".toUShort(16),7u,"D".toUShort(16),9u,6u,"B".toUShort(16),2u,0u,5u)
        return array[num.toInt()]
    }

    private fun subShort(num:UShort):UShort {
        var result:UShort = 0u
        var mask = "000F".toUShort(16)
        for (i in 0 .. 3) {
            //substituting block by block
            var block = num and mask
            block = (block.toInt() shr (i*4)).toUShort()
            result = result or (subBlock(block).toInt() shl (i*4)).toUShort()
            mask = (mask.toInt() shl (4)).toUShort()
        }
        return result
    }

    private fun revSubShort(num:UShort):UShort {
        var result:UShort = 0u
        var mask = "000F".toUShort(16)
        for (i in 0 .. 3) {
            //rev-substituting block by block
            var block = num and mask
            block = (block.toInt() shr (i*4)).toUShort()
            result = result or (revSubBlock(block).toInt() shl (i*4)).toUShort()
            mask = (mask.toInt() shl (4)).toUShort()
        }
        return result
    }

    private fun perm(num: UShort): UShort {
        var result:UShort = 0u
        if (num and (1 shl 0 ).toUShort() != 0.toUShort()) result= result or (1 shl 0 ).toUShort()
        if (num and (1 shl 1 ).toUShort() != 0.toUShort()) result= result or (1 shl 4 ).toUShort()
        if (num and (1 shl 2 ).toUShort() != 0.toUShort()) result= result or (1 shl 8 ).toUShort()
        if (num and (1 shl 3 ).toUShort() != 0.toUShort()) result= result or (1 shl 12).toUShort()
        if (num and (1 shl 4 ).toUShort() != 0.toUShort()) result= result or (1 shl 1 ).toUShort()
        if (num and (1 shl 5 ).toUShort() != 0.toUShort()) result= result or (1 shl 5 ).toUShort()
        if (num and (1 shl 6 ).toUShort() != 0.toUShort()) result= result or (1 shl 9 ).toUShort()
        if (num and (1 shl 7 ).toUShort() != 0.toUShort()) result= result or (1 shl 13).toUShort()
        if (num and (1 shl 8 ).toUShort() != 0.toUShort()) result= result or (1 shl 2 ).toUShort()
        if (num and (1 shl 9 ).toUShort() != 0.toUShort()) result= result or (1 shl 6 ).toUShort()
        if (num and (1 shl 10).toUShort() != 0.toUShort()) result= result or (1 shl 10).toUShort()
        if (num and (1 shl 11).toUShort() != 0.toUShort()) result= result or (1 shl 14).toUShort()
        if (num and (1 shl 12).toUShort() != 0.toUShort()) result= result or (1 shl 3 ).toUShort()
        if (num and (1 shl 13).toUShort() != 0.toUShort()) result= result or (1 shl 7 ).toUShort()
        if (num and (1 shl 14).toUShort() != 0.toUShort()) result= result or (1 shl 11).toUShort()
        if (num and (1 shl 15).toUShort() != 0.toUShort()) result= result or (1 shl 15).toUShort()
        //setting every bit according to the definition

        return result
    }
    private fun encrypt(clearText:UShort, key:UShort):UShort {
        var text = clearText

        text = text xor key
        text = subShort(text)
        text = perm(text)

        text = text xor key
        text = subShort(text)
        text = perm(text)

        text = text xor key
        text = subShort(text)
        text = perm(text)

        text = text xor key
        text = subShort(text)
        text = text xor key

        return text
    }

    private fun decrypt(cryptoText:UShort, key:UShort):UShort {
        var text = cryptoText

        text = text xor key
        text = revSubShort(text)
        text = text xor key

        text = perm(text)
        text = revSubShort(text)
        text = text xor key

        text = perm(text)
        text = revSubShort(text)
        text = text xor key

        text = perm(text)
        text = revSubShort(text)
        text = text xor key

        return text
    }

    fun getCryptoCleartextPairs(length:Int, random: Random, key:UShort):UIntArray {
        val pairs = UIntArray(length)
        for (i in 0 until length) {
            val chunk = random.nextBits(16).toUShort()
            pairs[i] = chunk.toUInt()
            val enc = encrypt(chunk,key)
            pairs[i] = pairs[i] or (enc.toUInt() shl 16)
        }
        //higher order 16 bits are crypto-text, lower order 16 bits are the corresponding clear-text
        return pairs
    }

    private fun getCleartext(pair:UInt):UShort {
        return (pair and "FFFF".toUInt(16)).toUShort()
    }

    private fun getCryptoText(pair:UInt):UShort {
        return (pair shr 16).toUShort()
    }

    fun getKeyByte(pairs:UIntArray):UByte {
        val keyFrequency = (0.."FF".toInt(16)).associateWith { 0 }.toMutableMap()

        for (key1 in 0..15){
            for (key2 in 0..15) {
                //iterating over all possible combinations of key-blocks
                for (pair in pairs) {
                    //iterating over all pairs
                    val cryptoText = getCryptoText(pair)
                    val clearText = getCleartext(pair)

                    val v4a = (key1 xor (cryptoText.toInt() shr 8)).toUShort() and "F".toUShort(16) //2nd block
                    val v4b = (key2.toUShort() xor cryptoText) and "F".toUShort(16) // 4th block
                    //the 4-bit blocks with the last key added

                    val u4a = revSubBlock(v4a)
                    val u4b = revSubBlock(v4b)
                    //the 4-bit blocks with the last substitution reversed

                    val u46 = getKthBit(u4a,2)
                    val u48 = getKthBit(u4a, 0)
                    val u414 = getKthBit(u4b, 2)
                    val u416 = getKthBit(u4b,0)
                    //the bits from the previous calculations that we actually care about

                    val x5 = getKthBit(clearText,11)
                    val x7 = getKthBit(clearText,9)
                    val x8 = getKthBit(clearText,8)
                    //the important clear-text bits


                    if (x5 xor x7 xor x8 xor u46 xor u48 xor u414 xor u416 == 0.toUShort()) { //our approximation
                        val unifiedKey = (key1 shl 4) or key2
                        //representing the two 16-bit keys as one int again
                        keyFrequency[unifiedKey] = keyFrequency[unifiedKey]!! + 1
                        //increasing the map value in order to save the result
                    }
                }
            }
        }
         return keyFrequency.entries.map { it.key to abs(it.value - pairs.size/2.0) }.sortedByDescending { it.second }[0].first.toUByte()
        // get the key-byte with the highest/lowest number of 'hits'
    }

    private fun getKthBit(num:UShort, k:Int):UShort {
        return ((num.toInt() and (1 shl k)) shr k).toUShort()
    }
}