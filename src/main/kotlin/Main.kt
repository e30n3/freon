import freon.Freon
import math.Formulas
import kotlin.reflect.full.primaryConstructor

fun main(args: Array<String>) {

    /** Тип фреона */
    var freonIndex = -1
    /**
     * Пока введеное значение фреона не окажется в допустимых индексах класса фреона
     * запрашивается новое значение индекса
     * */
    while (freonIndex !in Freon::class.sealedSubclasses.indices) {
        println("Укажите тип фреона:")
        /** Вывод всех возможных фреонов и их индексов*/
        Freon::class.sealedSubclasses.forEachIndexed { i, it ->
            print("$i. ${it.simpleName!!.removePrefix("Freon")}\t\t")
        }
        println()
        /**
         *  Читаем индекс фреона, введенный пользователем, если он некорректен то значение
         *  индекса фреона остаётся вне отрезка допустимых индексов (-1) и запрос ввода данных
         *  запуститься сначала
         * */
        freonIndex = readln().toIntOrNull() ?: -1
    }

    /** Диаметр капли*/
    var dropDiameter = -1.0
    /**
     *  Пока введенное значени диаметра капли не будет корректным (больше 0)
     *  запрашивается новое значение диаметра капли
     * */
    while (dropDiameter <= 0) {
        println("Укажите диаметр капли в мм:")
        /**
         * При невозможности пробразовать введенные данные в число возвращается отрицательное
         * значение капли для повторного входа в цикла и нового запроса диаметра
         * диаметр в консоли вводиться в мм, после его сразу же преобразуется в м
         * для последующих расчётов
         * */
        dropDiameter = (readln().toDoubleOrNull() ?: -1.0) / 1000 // из мм в м
    }

    /** Температура воздуха, по умолчанию не определена (NaN) */
    var t = Double.NaN
    /**
     *  Пока температура не будет введена в отрезке от -30 до 30 выполняем запрос нового
     *  значения температуры
     * */
    while (t !in -30.0..30.0) {
        println("Укажите температуру воздуха в градусах [-30; 30]:")
        /**
         * При невозможности преобразовать введенные данные в число возвразается
         * неопределенное значение для повторного входа в цикл и нового запроса температуры
         * */
        t = readln().toDoubleOrNull() ?: Double.NaN
    }

    /**
     *  Фреон
     *  Создаётся на основе выбранного индекса из доступных фреонов
     * */
    val freon = Freon::class.sealedSubclasses[freonIndex].primaryConstructor!!.call(t)

    /**
     *  Критерий архимеда
     *  Создаётся на основе описанной формулы расчёта критерия Архимеда
     * */
    val archimedesCriterion = Formulas.archimedesCriterion(dropDiameter, freon)

    /**
     *  Критерий Рейнольдса
     *  Создаётся на основе описанной формулы расчёта критерия Рейнольдса
     * */
    val reynoldsCriterion = Formulas.reynoldsCriterion(archimedesCriterion)

    /**
     *  Скорость витания пара
     *  Создаётся на основе описанной формулы расчёта скорости витания пара
     * */
    val steamSpeed =
        Formulas.steamSpeed(freon.kinematicVaporViscosity, dropDiameter, reynoldsCriterion)


    /**
     * Вывод расчитанных данных
     * */

    println("Критерий Архимеда:")
    println(archimedesCriterion)

    println("Критерий Рейнольдса:")
    println(reynoldsCriterion)

    println("Скорость пара:")
    println(steamSpeed)

    /**
     * Запрос ввода, для предотвращения преждевременного закрытия консоли
     * */
    readln()
}