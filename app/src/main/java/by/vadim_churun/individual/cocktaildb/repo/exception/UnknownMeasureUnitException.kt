package by.vadim_churun.individual.cocktaildb.repo.exception


class UnknownMeasureUnitException(
    val representation: String
): Exception("Unknown ingredient represented with string \"$representation\"")