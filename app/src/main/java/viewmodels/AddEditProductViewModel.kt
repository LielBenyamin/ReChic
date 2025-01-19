package viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.database.local.entities.UserProfileEntity
import com.example.rechic.repository.ImageRepository
import com.example.rechic.repository.ProductRepository
import com.example.rechic.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddEditProductViewModel(
    private val productRepository: ProductRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val productImageUri = MutableStateFlow<Uri?>(null)
    val productUri: StateFlow<Uri?>
        get() = productImageUri

    private val _upadteState = MutableSharedFlow<FireBaseState>()
    val upadteState: SharedFlow<FireBaseState> get() = _upadteState

    fun updateProductImageUri(uri: Uri) {
        productImageUri.value = uri
    }

    fun onDoneClicked(
        product: ProductEntity?,
        productName: String,
        productDescription: String,
        productPrice: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        _upadteState.emit(FireBaseState.Loading)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            _upadteState.emit(FireBaseState.Error("Internal Error"))
            return@launch
        }
        if (product != null) {
            onEditModeDone(
                product = product,
                productName = productName,
                productDescription = productDescription,
                productPrice = productPrice,
                uid = uid,
            )

        } else {
            onCreateModeDone(
                productName = productName,
                productDescription = productDescription,
                productPrice = productPrice,
                uid = uid,
            )
        }
    }

    private suspend fun onEditModeDone(
        product: ProductEntity,
        productName: String,
        productDescription: String,
        uid: String,
        productPrice: String,

        ) {
        var productUpdatedEntity = product
        val productUri = productUri.value
        val listFields = mutableListOf<String>()
        val listValues = mutableListOf<Any>()
        if (productName != product.name) {
            listFields.add(ProductEntity::name.name)
            listValues.add(productName)
            productUpdatedEntity = productUpdatedEntity.copy(
                name = productName,
            )
        }
        if (productDescription != product.description) {
            listFields.add(ProductEntity::description.name)
            listValues.add(productDescription)
            productUpdatedEntity = productUpdatedEntity.copy(
                description = productDescription,
            )
        }
        if (productPrice != product.price.toString()) {
            listFields.add(ProductEntity::price.name)
            listValues.add(productPrice.toDouble())
            productUpdatedEntity = productUpdatedEntity.copy(
                price = productPrice.toDouble(),
            )
        }
        if (productUri != null) {
            val productImgUrl = imageRepository.uploadProductImage(
                uid,
                productUri
            )
            if (productImgUrl != null) {
                listFields.add(UserProfileEntity::profileImageUrl.name)
                listValues.add(productImgUrl)
                productUpdatedEntity = productUpdatedEntity.copy(
                    imgUrl = productImgUrl,
                )
            }
        }
        if (listFields.isNotEmpty()) {
            productRepository.updateProduct(
                product = productUpdatedEntity,
                productId = product.productDocumentId,
                keys = listFields,
                values = listValues,
            )
            _upadteState.emit(FireBaseState.Success)
        } else {
            _upadteState.emit(FireBaseState.Error("No changes made"))
        }

    }

    private suspend fun onCreateModeDone(
        productName: String,
        productDescription: String,
        productPrice: String,
        uid: String,
    ) {
        val productUri = productImageUri.value
        if (ValidationUtils.areFieldsEmpty(
                productName,
                productDescription,
                productPrice
            )
            || productUri == null
        ) {
            _upadteState.emit(FireBaseState.Error("Fields cannot be empty"))
            return
        }
        val productImgUrl = imageRepository.uploadProductImage(
            uid,
            productUri
        )
        if (productImgUrl == null) {
            _upadteState.emit(FireBaseState.Error("Failed to upload image"))
            return
        }

        val product = ProductEntity(
            name = productName,
            description = productDescription,
            price = productPrice.toDouble(),
            imgUrl = productImgUrl,
            ownerUid = uid,
            productDocumentId = "",
        )

        productRepository.saveProduct(product)
        _upadteState.emit(FireBaseState.Success)
    }
}