package com.example.kata.rpg

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CharacterTest {
    @Test
    fun `character has the initial values`() {
        val initialHealth = 1000
        val character = Character(initialHealth)

        assertThat(character.health).isEqualTo(initialHealth)
        assertThat(character.level).isEqualTo(1)
    }

    @Test
    fun `character by default is alive`() {
        val character = Character()

        assertThat(character.alive).isTrue()
    }

    @Test
    fun `character can be killed`() {
        val character = Character()

        character.receiveDamage(1001)

        assertThat(character.alive).isFalse()
    }

    @Test
    fun `character can withstand damage without dying`() {
        val character = Character()

        character.receiveDamage(900)

        assertThat(character.alive).isTrue()
    }

    @Test
    fun `character can be healed, but not over the limit`() {
        val character = Character()

        character.heal(100)

        assertThat(character.health).isEqualTo(1000 + 0)
    }

    @Test
    fun `character can be healed`() {
        val character = Character(950)

        character.heal(100)

        assertThat(character.health).isEqualTo(950 + 50)
    }


    @Test
    fun `dead character cannot be healed`() {
        val deadCharacter = Character(0)

        deadCharacter.heal(200)

        assertThat(deadCharacter.alive).isFalse()
    }
}

class Character(var health: Int = INITIAL_HEALTH) {
    companion object {
        private val INITIAL_HEALTH = 1000
        private val MAXIMUM_HEALTH = 1000
    }

    val level: Int = 1
    val alive: Boolean
        get() = health > 0

    fun receiveDamage(damage: Int) {
        health -= damage
    }


    fun heal(amount: Int) {
        if (!alive) return
        health = Math.min(MAXIMUM_HEALTH, health + amount)
    }
}
