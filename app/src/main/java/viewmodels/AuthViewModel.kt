package viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechic.database.local.entities.UserProfileEntity

import com.example.rechic.repository.ImageRepository
import com.example.rechic.repository.UserRepository
import com.example.rechic.utils.ValidationUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _authState = MutableSharedFlow<FireBaseState>()
    val authState: SharedFlow<FireBaseState> get() = _authState

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> get() = _profileImageUri

    // Update image URI method
    fun updateProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
    }

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    fun updateSelectedLocation(latLng: LatLng) {
        _selectedLocation.value = latLng
    }

    fun login(username: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        _authState.emit(FireBaseState.Loading)
        if (username.isEmpty() || password.isEmpty()) {
            _authState.emit(FireBaseState.Error("Fields cannot be empty"))
            return@launch
        }

        try {
            firebaseAuth.signInWithEmailAndPassword(username, password).await()
            _authState.emit(FireBaseState.Success)
        } catch (e: Exception) {
            _authState.emit(FireBaseState.Error(e.localizedMessage ?: "Login failed"))
        }
    }

    fun registerUser(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String,
        phoneNumber: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _authState.emit(FireBaseState.Loading)
        if (ValidationUtils.areFieldsEmpty(phoneNumber, email, password, confirmPassword, userName)) {
            _authState.emit(FireBaseState.Error("All fields are required"))
            return@launch
        }
        val location: LatLng? = selectedLocation.value
        if (location == null) {
            _authState.emit(FireBaseState.Error("You must choose location"))
            return@launch
        }
        val profileImageUri = profileImageUri.value
        if (profileImageUri == null) {
            _authState.emit(FireBaseState.Error("You must choose profile photo"))
            return@launch
        }

        if (password != confirmPassword) {
            _authState.emit(FireBaseState.Error("Passwords do not match"))
            return@launch
        }

        if (!ValidationUtils.isValidEmail(email)) {
            _authState.emit(FireBaseState.Error("Invalid email format"))
            return@launch
        }

        if (!ValidationUtils.isValidPhoneNumber(phoneNumber)) {
            _authState.emit(FireBaseState.Error("Invalid phone number format"))
            return@launch
        }

        try {
            val usernameExists = userRepository.checkIfUsernameExists(userName)
            if (usernameExists) {
                _authState.emit(FireBaseState.Error("Username already exists"))
                return@launch
            }
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid
            if (userId == null) {
                _authState.emit(FireBaseState.Error("Something went wrong"))
                return@launch
            }

            val profileImageUrl = imageRepository.uploadProfileImage(userId, profileImageUri)
            if (profileImageUrl == null) {
                _authState.emit(FireBaseState.Error("Failed to upload image"))
                return@launch
            }

            val user = UserProfileEntity(
                userName = userName,
                email = email,
                profileImageUrl = profileImageUrl,
                location = location,
                phoneNumber = phoneNumber,
                userUid = userId,
            )

            userRepository.saveUser(
                user = user,
            )
            _authState.emit(FireBaseState.Success)

        } catch (e: Exception) {
            _authState.emit(FireBaseState.Error("Registration failed: ${e.localizedMessage}"))
        }
    }
}
