package uk.nhs.riskscore.internal

/**
 * Pads an array with an entry produced by `makeEntry` so that each index in the array from 0 to the
 * maximum value of the `index` function is assigned.
 * Assumptions:
 *  - The array is sorted ascending by the index function.
 *  - indexFn, restricted to elements of the list, is injective
 *
 * Example:
 *
 *      ['a', 'b', 'e'].pad({ (it + 97).toChar() }, { it.toInt() - 97 })
 *      // ['a', 'b', 'c', 'd', 'e']
 */
internal fun <E> List<E>.pad(makeEntry: (Int) -> E, indexFn: (E) -> Int): List<E> {
    return fold(mutableListOf()) { result, next ->
        val upto = indexFn(next)
        val top = result.size
        result.addAll((top until upto).map(makeEntry))
        result.add(next)
        result
    }
}