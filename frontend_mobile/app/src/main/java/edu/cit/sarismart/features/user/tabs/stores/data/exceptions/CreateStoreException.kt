package edu.cit.sarismart.features.user.tabs.stores.data.exceptions

sealed class CreateStoreException(override val message: String? = null, override val cause: Throwable? = null) : Exception() {
    object MissingOwnerId : CreateStoreException("User owner ID is not available.") {
        private fun readResolve(): Any = MissingOwnerId
    }

    data class InvalidInput(override val message: String) : CreateStoreException(message)

    object EmptyResponseBody : CreateStoreException("Empty response body from the server.") {
        private fun readResolve(): Any = EmptyResponseBody
    }

    data class ApiException(val code: Int, override val message: String?) : CreateStoreException("API error occurred (Code: $code, Message: $message)")

    data class UnknownError(override val cause: Throwable) : CreateStoreException("An unexpected error occurred.", cause)
}