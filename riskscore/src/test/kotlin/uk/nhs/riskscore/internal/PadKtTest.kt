package uk.nhs.riskscore.internal

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

internal class PadKtTest : StringSpec({
    "List.pad can be used to pad list of Char" {
        listOf('b', 'e').pad({ (it + 97).toChar() }, { it.toInt() - 97 }) shouldBe listOf('a', 'b', 'c', 'd', 'e')
    }

    "List.pad does not pad the empty list" {
        emptyList<Char>().pad({ (it + 97).toChar() }, { it.toInt() - 97 }) shouldBe emptyList()
    }

    "List.pad the size of the returned list is equal to the input list" {
        checkAll(Arb.list(Arb.int(0 until 1000))) { l ->
            l.sorted().distinct().pad(::identity, ::identity).size shouldBeExactly (l.max()?.inc() ?: 0)
        }
    }
})