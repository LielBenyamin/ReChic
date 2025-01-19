package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechic.repository.ProductRepository
import com.example.rechic.repository.UserRepository
import kotlinx.coroutines.launch

class HomeActivityViewModel(
    private val productsRepository: ProductRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    fun syncData() {
        viewModelScope.launch {
            userRepository.syncUsers()
            productsRepository.syncProducts()
        }
    }
}
