package math

import freon.Freon
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Объект с формулами и константами, необходимыми для расчёта
 * @property G ускорение свободного падения
 * */
object Formulas {
    private const val G = 9.80665

    /**
     *  Формула расчёта критерия Архимеда
     *  @param dropDiameter диаметр капли в м
     *  @param freon фреон
     *  @return значение критерия Архимеда
     * */
    fun archimedesCriterion(dropDiameter: Double, freon: Freon) =
        ((G * (dropDiameter.pow(3))) / (freon.kinematicVaporViscosity.pow(2))) *
                ((freon.liquidDensity - freon.vaporDensity) / (freon.vaporDensity))

    /**
     * Формула расчёта критерия Рейнольдса для витания
     * @param archimedesCriterion критерий Архимеда
     * @return значения критерия Рейнольдса
     * */
    fun reynoldsCriterion(archimedesCriterion: Double) =
        (archimedesCriterion) / (18 + 0.61 * sqrt(archimedesCriterion))

    /**
     * Формула расчёта скорости витания пара
     * @param kinematicVaporViscosity кинематическая вязкость пара
     * @param dropDiameter диаметр капли в м
     * @param reynoldsCriterion значение критерия Рейнольдса
     * */
    fun steamSpeed(
        kinematicVaporViscosity: Double,
        dropDiameter: Double,
        reynoldsCriterion: Double
    ) =
        (reynoldsCriterion * kinematicVaporViscosity) / dropDiameter
}