package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechic.model.Country
import com.example.rechic.model.Flags
import com.example.rechic.model.Idd
import com.example.rechic.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CountryViewModel(private val countryRepository: CountryRepository) : ViewModel() {

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> get() = _countries

    init {
        viewModelScope.launch {
            try {
                val countries = countryRepository.getCountries()
                countries.sortedBy {
                    it.getFirstSuffixAsNumber()
                }.let {
                    _countries.value = it
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}