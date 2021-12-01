import kotlin.random.Random

@ExperimentalUnsignedTypes
fun main() {
    val spn = SubstitutionPermutationNetwork()
    val random = Random(69)
    val key = random.nextBits(16).toUShort()
    val pairs = spn.getCryptoCleartextPairs(10000,random,key)
    val calcByte = spn.getKeyByte(pairs)
    val keyString = "0".repeat(16-key.toString(2).length)+key.toString(2)
    println("full key: $keyString")
    println("relevant byte:   ${keyString.subSequence(4,8).toString()+keyString.subSequence(12,16)}")
    println("calculated byte: ${"0".repeat(8-calcByte.toString(2).length)+calcByte.toString(2)}")

}