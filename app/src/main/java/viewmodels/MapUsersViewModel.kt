package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechic.database.local.entities.UserProfileEntity
import com.example.rechic.model.ProductWithUserProfile
import com.example.rechic.repository.ProductRepository
import com.example.rechic.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapUsersViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    val allUsers: StateFlow<List<UserProfileEntity>> =
        userRepository.getAllUsersFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
