package be.brammeerten.y2023

import be.brammeerten.readFileAndSplitLines
import be.brammeerten.toCharList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


val POWERS: Map<Char, Int> = mapOf(
    'A' to 13,
    'K' to 12,
    'Q' to 11,
    'J' to 10,
    'T' to 9,
    '9' to 8,
    '8' to 7,
    '7' to 6,
    '6' to 5,
    '5' to 4,
    '4' to 3,
    '3' to 2,
    '2' to 1
)
val POWERS2: Map<Char, Int> = mapOf(
    'A' to 13,
    'K' to 12,
    'Q' to 11,
    'T' to 9,
    '9' to 8,
    '8' to 7,
    '7' to 6,
    '6' to 5,
    '5' to 4,
    '4' to 3,
    '3' to 2,
    '2' to 1,
    'J' to 0
)

class Day7Test {

    @Test
    fun `part 1`() {
        val sum = readFileAndSplitLines("2023/day7/exampleInput.txt", " ")
            .map { Hand(it[0].toCharList(), it[1].toInt()) }
            .sortedBy { it }
            .mapIndexed { i, hand -> (i + 1) * hand.bid }
            .sum()
        assertThat(sum).isEqualTo(6440);
//         assertThat(sum).isEqualTo(250946742);
    }

    @Test
    fun `part 2`() {
        val sum = readFileAndSplitLines("2023/day7/exampleInput.txt", " ")
            .map { Hand2(it[0].toCharList(), it[1].toInt()) }
            .sortedBy { it }
            .mapIndexed { i, hand -> (i + 1) * hand.bid }
            .sum()
        assertThat(sum).isEqualTo(5905);
//         assertThat(sum).isEqualTo(251824095);
    }

    data class Hand(val cards: List<Char>, val bid: Int, val strength: Int) : Comparable<Hand> {
        constructor(cards: List<Char>, bid: Int) : this(cards, bid, calcStrength(cards))

        override fun compareTo(other: Hand): Int {
            if (strength < other.strength) return -1
            if (strength > other.strength) return 1

            for (i in cards.indices) {
                if (POWERS[cards[i]]!! < POWERS[other.cards[i]]!!) return -1
                if (POWERS[cards[i]]!! > POWERS[other.cards[i]]!!) return 1
            }

            throw RuntimeException("SHOULD NEVER HAPPEN")
        }
    }

    data class Hand2(val cards: List<Char>, val bid: Int, val strength: Int) : Comparable<Hand2> {
        constructor(cards: List<Char>, bid: Int) : this(cards, bid, calcStrength2(cards))

        override fun compareTo(other: Hand2): Int {
            if (strength < other.strength) return -1
            if (strength > other.strength) return 1

            for (i in cards.indices) {
                if (POWERS2[cards[i]]!! < POWERS2[other.cards[i]]!!) return -1
                if (POWERS2[cards[i]]!! > POWERS2[other.cards[i]]!!) return 1
            }

            throw RuntimeException("SHOULD NEVER HAPPEN")
        }
    }
}

fun calcStrength(cards: List<Char>): Int {
    val counts = hashMapOf<Char, Int>()
    var max = 0
    for (card in cards) {
        val c = counts.getOrDefault(card, 0)
        counts[card] = c + 1
        if (c + 1 > max) max = c + 1
    }

    if (max >= 4) return max + 1
    if (max == 3) {
        // full house
        return if (counts.values.contains(2)) 4 else 3
    }
    if (max == 2) {
        if (counts.values.count { it == 2 } == 2) return 2
    }
    return max - 1
}


fun calcStrength2(cards: List<Char>): Int {
    val counts = hashMapOf<Char, Int>()
    var max = 0
    var jokers = 0
    for (card in cards) {
        if (card == 'J') {
            jokers++
            continue
        }
        val c = counts.getOrDefault(card, 0)
        counts[card] = c + 1
        if (c + 1 > max) max = c + 1
    }

    if ((max + jokers) >= 4)
        return (max + jokers) + 1
    if ((max + jokers) == 3) {
        if (jokers >= 2) return 3 // three of a kind, not full house, otherwise would have had 4 of a kind
        if (jokers == 1) {
            return if (counts.values.count { it == 2 } == 2)
                4 // full house
            else
                3
        }
        if (counts.values.contains(2))
            return 4 // full house
        else
            return 3
    }
    if (jokers > 1) throw java.lang.RuntimeException("TOO MANY JOKERS?")
    if ((max + jokers) == 2) {
        if (jokers == 0) {
            if (counts.values.count { it == 2 } == 2) return 2
        } else {
            if (counts.values.count { it == 2 } == 1) throw java.lang.RuntimeException("ALSO DID NOT EXPECT: " + cards)
        }
    }
    return (max + jokers) - 1
}
