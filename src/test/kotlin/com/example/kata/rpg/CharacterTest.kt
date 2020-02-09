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

    @Test
    fun `character can attack an enemy`() {
        val character = Character()
        val enemyHealth = 1000
        val enemy = Character(enemyHealth)

        character.attack(enemy, 500)

        assertThat(enemy.health).isEqualTo(enemyHealth - 500)
    }

    @Test
    fun `character can't attack himself`() {
        val initialHealth = 1000
        val character = Character(initialHealth)

        character.attack(character, 500)

        assertThat(character.health).isEqualTo(initialHealth)
    }

    @Test
    fun `character will deal less damage if the enemy is five levels above`() {
        val character = Character()
        val enemyHealth = 1000
        val enemyLevel = 6
        val enemy = Character(enemyHealth, enemyLevel)

        character.attack(enemy, 500)

        assertThat(enemy.health).isEqualTo(enemyHealth - (500 / 2))
    }

}

class DamageEffectTest {
    @Test
    fun `damage effect will be reduced`() {
        val damageEffect = damageEffectOf(1, 6, 2)

        assertThat(damageEffect.amount).isEqualTo(1)
    }

    @Test
    fun `damage effect will stay the same`() {
        val damageEffect = damageEffectOf(1, 1, 2)

        assertThat(damageEffect.amount).isEqualTo(2)
    }

    @Test
    fun `damage effect will stay the same as long as the level difference is not 5`() {
        val damageEffect = damageEffectOf(1, 5, 2)

        assertThat(damageEffect.amount).isEqualTo(2)
    }

    @Test
    fun `damage effect will be boosted when the difference in levels (case 1)`() {
        val damageEffect = damageEffectOf(6, 1, 2)

        assertThat(damageEffect.amount).isEqualTo((2 * 1.5).toInt())
    }

    @Test
    fun `damage effect will be boosted when the difference in levels (case 2)`() {
        val damageEffect = damageEffectOf(7, 1, 2)

        assertThat(damageEffect.amount).isEqualTo((2 * 1.5).toInt())
    }

    @Test
    fun `damage effect will stay the same (case 2)`() {
        val damageEffect = damageEffectOf(4, 1, 2)

        assertThat(damageEffect.amount).isEqualTo(2)
    }
}

class DamageEffect(val amount: Int) {

}

class Character(health: Int = INITIAL_HEALTH, val level: Int = 1) {
    companion object {
        private val INITIAL_HEALTH = 1000
        private val MAXIMUM_HEALTH = 1000
    }

    var health = health
        private set
    val alive: Boolean
        get() = health > 0

    fun receiveDamage(damage: Int) {
        health -= damage
    }


    fun heal(amount: Int) {
        if (!alive) return
        health = Math.min(MAXIMUM_HEALTH, health + amount)
    }

    fun attack(enemy: Character, damage: Int) {
        if (enemy == this) return

        enemy.receiveDamage(damageEffectOf(level, enemy.level, damage).amount)
    }
}

fun damageEffectOf(characterLevel: Int, enemyLevel: Int, damage: Int): DamageEffect = when {
    characterLevel.overpowers(enemyLevel) -> DamageEffect((damage * 1.5).toInt())
    enemyLevel.overpowers(characterLevel) -> DamageEffect(damage / 2)
    else -> DamageEffect(damage)
}

private fun Int.overpowers(enemyLevel: Int) = this - enemyLevel >= 5
