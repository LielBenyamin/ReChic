package viewmodels

sealed class FireBaseState {
    data object Success : FireBaseState()
    data class Error(val message: String) : FireBaseState()
    data object Loading : FireBaseState()
}