package freon

import interpolation.Interpolator
import math.Formulas
import kotlin.math.pow
import kotlin.reflect.full.primaryConstructor

/**
 * Абстрактный класс Фреона
 *
 * Использует модификатор sealed (запечатанный) для представления ограниченной иерархии классов,
 * для обеспечивания большего контроля над наследованием.
 *
 * Абстрактный класс для расчётов использует делегат [lazy] в свох свойствах для оптимизации расчётов
 * (ресурсы компьютера для произведения расчётов будут задействованы только в момент первого обращения
 * к переменной)
 *
 * Классы наследники должны реализовать только таблицу фреона [tableValues]
 *
 * Свойства [vaporDensity], [liquidDensity], [dynamicVaporViscosity] для расчётов значения под
 * конкретную температуру [temperature] используют интерполяцию [Interpolator], на основе данных
 * таблицы фреона [tableValues]
 *
 *
 * @constructor Для создания фреона необходима только температура окружающей среды
 * @property tableValues табличные значения фреона, необходимые для расчётов,
 * представленна в виде [List]<[FreonTableRow]> (Список табличных строк фреона)
 * @property availableTemperature отрезок на котором может производиться расчёт характеристик фреона
 * @property vaporDensity плотность газообразного фреона
 * @property liquidDensity Плотность жидкого фреона
 * @property dynamicVaporViscosity Динамическая вязкость пара фреона
 * @property kinematicVaporViscosity Кинематическая вязкость пара фреона
 *
 * */
sealed class Freon(open var temperature: Double) {

    abstract val tableValues: List<FreonTableRow>

    val availableTemperature: ClosedFloatingPointRange<Double> by lazy {
        tableValues.minOf { it.temperature }..tableValues.minOf { it.temperature }
    }

    val vaporDensity: Double by lazy {
        Interpolator(tableValues.map { it.temperature }, tableValues.map { it.vaporDensity })
            .interpolate(temperature)
    }

    val liquidDensity: Double by lazy {
        Interpolator(tableValues.map { it.temperature }, tableValues.map { it.liquidDensity })
            .interpolate(temperature)
    }

    val dynamicVaporViscosity: Double by lazy {
        Interpolator(tableValues.map { it.temperature }, tableValues.map { it.vaporViscosity })
            .interpolate(temperature) * 10.0.pow(-6)
    }

    val kinematicVaporViscosity: Double by lazy {
        dynamicVaporViscosity / vaporDensity
    }

}


/**Фреон R134*/
data class FreonR134(override var temperature: Double) : Freon(temperature) {
    override val tableValues = listOf(
        FreonTableRow(-30.0, 9.74, 4.35, 1396.9),
        FreonTableRow(-20.0, 10.2, 6.71, 1363.2),
        FreonTableRow(-10.0, 10.7, 9.97, 1331.4),
        FreonTableRow(0.0, 11.2, null, 1298.7),
        FreonTableRow(10.0, 11.7, null, 1264.6),
        FreonTableRow(20.0, 12.2, null, 1228.4),
        FreonTableRow(30.0, 12.7, 37.89, 1190.1),
    )
}

/**Фреон R407*/
data class FreonR407(override var temperature: Double) : Freon(temperature) {
    override val tableValues = listOf(
        FreonTableRow(-30.0, 10.2, 6.017, 1338.83),
        FreonTableRow(-20.0, 10.11, 9.108, 1306.71),
        FreonTableRow(-10.0, 10.97, 13.328, 1273.11),
        FreonTableRow(0.0, 11.43, 18.947, 1237.76),
        FreonTableRow(10.0, 12.2, 26.299, 1200.33),
        FreonTableRow(20.0, 12.67, 35.817, 1160.36),
        FreonTableRow(30.0, 13.08, 48.108, 1117.2),
    )
}

/**Фреон R410*/
data class FreonR410(override var temperature: Double) : Freon(temperature) {
    override val tableValues = listOf(
        FreonTableRow(-30.0, 9.87, 2.683, 1278.534),
        FreonTableRow(-20.0, 10.54, 3.944, 1245.297),
        FreonTableRow(-10.0, 10.86, 5.635, 1209.914),
        FreonTableRow(0.0, 11.21, 7.849, 1171.968),
        FreonTableRow(10.0, 12.4, 10.688, 1130.887),
        FreonTableRow(20.0, 12.89, 14.26, 1085.849),
        FreonTableRow(30.0, 13.02, 18.681, 1035.603),
    )
}

/**Фреон R32*/
data class FreonR32(override var temperature: Double) : Freon(temperature) {
    override val tableValues = listOf(
        FreonTableRow(-30.0, 10.18, 7.61, 1151.0),
        FreonTableRow(-20.0, 10.61, 11.109, 1120.6),
        FreonTableRow(-10.0, 11.05, 15.801, 1088.8),
        FreonTableRow(0.0, 11.51, 21.997, 1055.3),
        FreonTableRow(10.0, 12.0, 30.101, 1019.7),
        FreonTableRow(20.0, 12.86, 40.66, 981.4),
        FreonTableRow(30.0, 13.58, 54.468, 939.6),
    )
}

fun main() {
    mixed()
}

fun mixed() {
    println("Укажите температуру воздуха в градусах:")
    /** Температура воздуха */
    val t = readln().toDouble()


    Freon::class.sealedSubclasses.forEach {
        println(it.simpleName)
        val freon = (it.primaryConstructor!!.call(t))
        val start = 0.1
        val end = 2.0
        val step = 0.05
        var currentDropDiameter = start
        while (currentDropDiameter in start..end) {

            val currentDropDiameterInMeter =
                currentDropDiameter / 1000
            val archimedesCriterion =
                Formulas.archimedesCriterion(currentDropDiameterInMeter, freon)
            val reynoldsCriterion =
                Formulas.reynoldsCriterion(archimedesCriterion)
            val steamSpeed =
                Formulas.steamSpeed(freon.kinematicVaporViscosity, currentDropDiameterInMeter, reynoldsCriterion)

            println("$steamSpeed".replace(".", ","))

            currentDropDiameter += step
        }
        println()
    }
}

fun tempRange() {
    val startTemp = -10
    val endTemp = 15
    val stepTemp = 5
    var currentTemp = startTemp
    while (currentTemp in startTemp..endTemp) {
        println("t=$currentTemp")
        val freon = FreonR410(currentTemp.toDouble())
        val start = 0.1
        val end = 2.0
        val step = 0.05
        var currentDropDiameter = start
        while (currentDropDiameter in start..end) {

            val currentDropDiameterInMeter =
                currentDropDiameter / 1000
            val archimedesCriterion =
                Formulas.archimedesCriterion(currentDropDiameterInMeter, freon)
            val reynoldsCriterion =
                Formulas.reynoldsCriterion(archimedesCriterion)
            val steamSpeed =
                Formulas.steamSpeed(freon.kinematicVaporViscosity, currentDropDiameterInMeter, reynoldsCriterion)

            println("$steamSpeed".replace(".", ","))

            currentDropDiameter += step
        }
        println()
        currentTemp += stepTemp
    }
}























