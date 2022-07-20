package interpolation

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import java.util.Collections.max
import java.util.Collections.min


/**
 * Интерполятор, использует кубическую сплайновую интерполяцию из [org.apache.commons.math3]
 *
 * @constructor Принимает два списка чисел с плавующей точкой x и y, количество элементов в этих
 * двух списках должно быть одинаковым. При этом y может содержать пропущенные значения (null), но
 * не в начале и не в конце. При наличие пропущенных (null) значений в y интерполятор автоматически
 * их расчитает на основе остальных данных в списке y
 *
 * @property safeX аналог списка x
 * @property safeY безопасный вариант списка y (обработаны пропуски (null) если есть)
 * @property splineFunction полиномиальная сплайн-функция, рассчитанная на основе [safeX] и [safeY]
 * необходимая для интерполяции значений x
 * */
class Interpolator(x: List<Double>, y: List<Double?>) {

    init {
        require(y.size == x.size) {
            "Размеры массивов должны совпадать! Размер X - ${x.size}, Размер Y - ${y.size}"
        }
        require(y[y.lastIndex] != null && y[0] != null) {
            "Первые и последние значения массива не должны быть null! Первое: ${y[0]} , Последнее: ${y[y.lastIndex]}"
        }
    }

    private val safeX: List<Double> = x
    private val safeY: List<Double> = y.filledNull
    private val splineFunction = SplineInterpolator().interpolate(safeX.toDoubleArray(), safeY.toDoubleArray())

    /**
     * Возвращает этот же список но с заполненными пропущенными значениями (null) используя
     * кубическую интерполяицю
     *
     * В основе лежит индексация элементов и построение полиномиальной функции для индексов
     * соотнесенных с существующими значениями.
     * */
    private val List<Double?>.filledNull: List<Double>
        get() = if (contains(null)) {
            val numbers = MutableList(size) { it.toDouble() }
            val indexes = mutableListOf<Int>()
            for (i in 0..lastIndex) if (get(i) == null)
                indexes.add(i)
            numbers.removeAll(indexes.map { it.toDouble() })
            val result = mutableListOf<Double>()
            SplineInterpolator().interpolate(numbers.toDoubleArray(), filterNotNull().toDoubleArray())
                .let { splineFunction ->
                    for (i in 0..lastIndex)
                        result.add(splineFunction.value(i.toDouble()))
                }
            result.toList()
        } else map { it!! }


    /**
     * Метод для интерполяции
     * @param x значение x для которого необходимо подобрать значение из y
     * @return подобранное значение y для [x]
     * */
    fun interpolate(x: Double): Double {
        require(x <= max(safeX) && x >= min(safeX)) {
            "Искомое значение должно лежать в промежутке от ${min(safeX)} до ${max(safeX)}"
        }
        return splineFunction.value(x)
    }

}

