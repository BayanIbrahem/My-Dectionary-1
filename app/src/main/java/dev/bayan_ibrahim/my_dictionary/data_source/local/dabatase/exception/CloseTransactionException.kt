package dev.bayan_ibrahim.my_dictionary.data_source.local.dabatase.exception

object CloseTransactionException: Exception("Room Transaction requested to be closed") {
    private fun readResolve(): Any = CloseTransactionException
}