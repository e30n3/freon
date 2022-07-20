package freon

/**
 * Набор табличных значений фреона под определенную температуру окржающей среды [temperature]
 * @property temperature температура окружающей среды
 * @property vaporViscosity Динамическая вязкость пара фреона
 * @property vaporDensity плотность газообразного фреона, может отсутствовать (быть null), в таком
 * случае будет определено примерное значение с помощью интерполяции в последующих расчётах
 * @property liquidDensity Плотность жидкого фреона
 * */
data class FreonTableRow(
    val temperature: Double,
    val vaporViscosity: Double,
    val vaporDensity: Double?,
    val liquidDensity: Double,
)