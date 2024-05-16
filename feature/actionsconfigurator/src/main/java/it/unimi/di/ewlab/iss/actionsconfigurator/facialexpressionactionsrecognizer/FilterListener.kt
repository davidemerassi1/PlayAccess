package it.unimi.di.ewlab.iss.actionsconfigurator.facialexpressionactionsrecognizer

interface FilterListener {
    fun onClassification(classification: Int)
}