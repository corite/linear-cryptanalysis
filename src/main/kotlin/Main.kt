import kotlin.random.Random

@ExperimentalUnsignedTypes
fun main() {
    val spn = SubstitutionPermutationNetwork()
    val random = Random(1)
    //I'm using a seed in order to make the results repeatable
    val key = random.nextBits(16).toUShort()
    val pairs = spn.getCryptoCleartextPairs(8000,random,key)
    //the number of pairs needed has been fluctuating in my testing depending on the key and the
    //pairs from about 8000  (seed 1) to 25000 (seed 42)
    val calcByte = spn.getKeyByte(pairs)
    val keyString = "0".repeat(16-key.toString(2).length)+key.toString(2)
    println("full key: $keyString")
    println("relevant key-byte:   ${keyString.subSequence(4,8).toString()+keyString.subSequence(12,16)}")
    println("calculated key-byte: ${"0".repeat(8-calcByte.toString(2).length)+calcByte.toString(2)}")

}