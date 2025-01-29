package viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.database.local.entities.UserProfileEntity
import com.example.rechic.model.ProductWithUserProfile
import com.example.rechic.repository.ImageRepository
import com.example.rechic.repository.ProductRepository
import com.example.rechic.repository.UserRepository
import com.example.rechic.utils.ValidationUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val productsRepository: ProductRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?>
        get() = _profileImageUri

    private val _upadteState = MutableSharedFlow<FireBaseState>()
    val upadteState: SharedFlow<FireBaseState> get() = _upadteState

    val userProfile: StateFlow<UserProfileEntity?> = userRepository.getUserProfileFlow(
        FirebaseAuth.getInstance().currentUser?.uid ?: ""
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val allUserProducts: StateFlow<List<ProductWithUserProfile>> =
        productsRepository.getAllProductsWithUserProfileByOwnerIdFlow(
            FirebaseAuth.getInstance().currentUser?.uid ?: ""
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )


    fun updateProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
    }

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
        val userProfile = userRepository.getCurrentUserProfile(uid)
        if (userProfile == null) {
            _upadteState.emit(FireBaseState.Error("Internal Error"))
            return@launch
        }
        var userProfileCopy = userProfile.copy()
        val listFields = mutableListOf<String>()
        val listValues = mutableListOf<Any>()
        if (userName != userProfile.userName) {
            if (userRepository.checkIfUsernameExists(userName)) {
                _upadteState.emit(FireBaseState.Error("Username already exists"))
                return@launch
            }
            listFields.add(UserProfileEntity::userName.name)
            listValues.add(userName)
            userProfileCopy = userProfileCopy.copy(userName = userName)
        }
        if (phoneNumber != userProfile.phoneNumber) {
            listFields.add(UserProfileEntity::phoneNumber.name)
            listValues.add(phoneNumber)
            userProfileCopy = userProfileCopy.copy(phoneNumber = phoneNumber)
        }
        val selectedLocaiton = _selectedLocation.value
        if (selectedLocaiton != null) {
            listFields.add(UserProfileEntity::location.name)
            listValues.add(selectedLocaiton)
            userProfileCopy = userProfileCopy.copy(location = selectedLocaiton)
        }
        val profileImageUri = _profileImageUri.value

        if (profileImageUri != null) {
            val profileImageUrl = imageRepository.uploadProfileImage(
                uid,
                profileImageUri
            )
            if (profileImageUrl != null) {
                listFields.add(UserProfileEntity::profileImageUrl.name)
                listValues.add(profileImageUrl)
                userProfileCopy = userProfileCopy.copy(profileImageUrl = profileImageUrl)
            }
        }
        if (listFields.isNotEmpty()) {
            userRepository.updateUserProfileFields(
                userId = uid,
                keys = listFields,
                values = listValues,
                userProfileCopy = userProfileCopy,
            )
            _upadteState.emit(FireBaseState.Success)
        } else {
            _upadteState.emit(FireBaseState.Error("No changes made"))
        }
    }

    fun onSignOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun deleteItem(productEntity: ProductEntity) {
        viewModelScope.launch {
            productsRepository.deleteProduct(productEntity)
        }
    }
}