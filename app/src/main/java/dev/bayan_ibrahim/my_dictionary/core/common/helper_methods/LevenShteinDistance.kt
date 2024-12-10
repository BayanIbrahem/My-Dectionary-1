package dev.bayan_ibrahim.my_dictionary.core.common.helper_methods

/**
 * [s1] and [s2] must be non-empty
 */
fun levensteinDistance(
    s1: String,
    s2: String,
    charDistance: (Char, Char) -> Float = { _, _ -> 1f },
): Float {
    if (s1 == s2) return 0f
    val d: MutableList<MutableList<Float?>> = initD(s1, s2, charDistance)

    val m = s1.length.dec()
    val n = s2.length.dec()
    val distance = d.calculateDij(m, n, s1[m], s2[n], charDistance)
    return distance
}

private fun initD(
    s1: String,
    s2: String,
    charDistance: (Char, Char) -> Float,
) = List(s1.length) {
    List<Float?>(s2.length) {
        null
    }.toMutableList()
}.toMutableList().apply {
    this[0][0] = charDistance(s1[0], s2[0])
}

private fun MutableList<MutableList<Float?>>.calculateDij(
    i: Int,
    j: Int,
    c1: Char,
    c2: Char,
    charDistance: (Char, Char) -> Float,
): Float {
    if (i < 0 || j < 0) return Float.MAX_VALUE
    if (this[i][j] == null) {
        val di1j = this@calculateDij.calculateDij(i.dec(), j, c1, c2, charDistance)
        val dij1 = this@calculateDij.calculateDij(i, j.dec(), c1, c2, charDistance)
        val di1j1 = this@calculateDij.calculateDij(i.dec(), j.dec(), c1, c2, charDistance)
        val (newI, newJ) = if (di1j < dij1 && di1j < di1j1) {
            Pair(i.dec(), j)
        } else if (dij1 < di1j && dij1 < di1j1) {
            Pair(i, j.dec())
        } else {
            Pair(i.dec(), j.dec())
        }
        val dist = charDistance(c1, c2)
        this[i][j] = dist + (this[newI][newJ] ?: 0f)
    }

    return this[i][j]!!
}
