package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.model.ProductWithUserProfile
import com.example.rechic.repository.ProductRepository
import com.example.rechic.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeFragmentViewModel(
    private val productsRepository: ProductRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val allProducts: StateFlow<List<ProductWithUserProfile>> =
        productsRepository.getAllProductsFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )


    fun getUserProducts(userUid: String): Flow<List<ProductWithUserProfile>> {
        return productsRepository.getAllProductsWithUserProfileByOwnerIdFlow(userUid)
    }

    private val _syncState = MutableStateFlow<Boolean>(false)
    val syncState: StateFlow<Boolean> = _syncState.asStateFlow()

    fun syncData() {
        viewModelScope.launch {
            _syncState.value = true
            userRepository.syncUsers()
            productsRepository.syncProducts()
            _syncState.value = false
        }
    }
}
