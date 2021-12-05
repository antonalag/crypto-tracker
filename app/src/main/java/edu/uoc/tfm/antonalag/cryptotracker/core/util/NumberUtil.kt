package edu.uoc.tfm.antonalag.cryptotracker.core.util

object NumberUtil {
    /**
     * Calculate rule of Three's Y value.
     *
     * A -----> B
     * X -----> Y
     * For more information about variable naming, visit: https://es.wikipedia.org/wiki/Regla_de_tres
     */
    fun ruleOfThreeCalculateYValue(aValue: Double, bValue: Int, xValue: Double): Double {
        return ((xValue * bValue)/aValue)
    }

    /**
     * Calculate rule of Three's X value.
     *
     * A -----> B
     * X -----> Y
     * For more information about variable naming, visit: https://es.wikipedia.org/wiki/Regla_de_tres
     */
    fun ruleOfThreeCalculateXValue(aValue: Double, bValue: Int, yValue: Double): Double {
        return ((yValue * aValue)/bValue)
    }
}