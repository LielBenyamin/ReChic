package viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechic.model.UserProfile
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

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?>
        get() = _profileImageUri

    private val _upadteState = MutableSharedFlow<FireBaseState>()
    val upadteState: SharedFlow<FireBaseState> get() = _upadteState

    val userProfile: StateFlow<UserProfile?>
        get() = _userProfile

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _userProfile.value = null
            _userProfile.value = userRepository.getCurrentUserLoggedInProfile()
        }
    }

    fun updateProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
    }


    private val _selectedLocation = MutableStateFlow<LatLng?>(null)

    fun updateSelectedLocation(latLng: LatLng) {
        _selectedLocation.value = latLng
    }

    fun onUpdatedClicked(
        userName: String,
        phoneNumber: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _upadteState.emit(FireBaseState.Loading)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            _upadteState.emit(FireBaseState.Error("Internal Error"))
            return@launch
        }
        if (ValidationUtils.areFieldsEmpty(userName, phoneNumber)) {
            _upadteState.emit(FireBaseState.Error("Fields cannot be empty"))
            return@launch
        }
        if (!ValidationUtils.isValidPhoneNumber(phoneNumber)) {
            _upadteState.emit(FireBaseState.Error("Invalid Phone Number"))
            return@launch
        }
        val userProfile = _userProfile.value
        if (userProfile == null) {
            _upadteState.emit(FireBaseState.Error("Internal Error"))
            return@launch
        }
        val listFields = mutableListOf<String>()
        val listValues = mutableListOf<Any>()
        if (userName != userProfile.userName) {
            if (userRepository.checkIfUsernameExists(userName)) {
                _upadteState.emit(FireBaseState.Error("Username already exists"))
                return@launch
            }
            listFields.add(UserProfile::userName.name)
            listValues.add(userName)
        }
        if (phoneNumber != userProfile.phoneNumber) {
            listFields.add(UserProfile::phoneNumber.name)
            listValues.add(phoneNumber)
        }
        val selectedLocaiton = _selectedLocation.value
        if (selectedLocaiton != null) {
            listFields.add(UserProfile::location.name)
            listValues.add(selectedLocaiton)
        }
        val profileImageUri = _profileImageUri.value

        if (profileImageUri != null) {
            val profileImageUrl = imageRepository.uploadProfileImage(
                uid,
                profileImageUri
            )
            if (profileImageUrl != null) {
                listFields.add(UserProfile::profileImageUrl.name)
                listValues.add(profileImageUrl)
            }
        }
        if (listFields.isNotEmpty()) {
            userRepository.updateUserProfileFields(
                userId = uid,
                keys = listFields,
                values = listValues,
            )
            _upadteState.emit(FireBaseState.Success)
            fetchUserProfile()
        } else {
            _upadteState.emit(FireBaseState.Error("No changes made"))
        }
    }
}