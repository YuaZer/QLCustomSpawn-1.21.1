package io.github.yuazer.qlcustomspawn.utils

object RandomUtils {
    fun pickByWeight(spawner: Map<String, Double>): String? {
        if (spawner.isEmpty()) return null

        val totalWeight = spawner.values.sum()
        val randomValue = Math.random() * totalWeight

        var cumulativeWeight = 0.0
        for ((key, weight) in spawner) {
            cumulativeWeight += weight
            if (randomValue < cumulativeWeight) {
                return key
            }
        }

        return null // 理论上不会触发，除非数据有误
    }

}