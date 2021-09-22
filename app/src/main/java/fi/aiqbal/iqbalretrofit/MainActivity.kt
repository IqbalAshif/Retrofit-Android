package fi.aiqbal.iqbalretrofit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers

class WikiRepository {
    private val call = NetworkApi.service
    suspend fun hitCountCheck(name: String) = call.userName(name)
}
class MainViewModel : ViewModel() {
   private val repository: WikiRepository = WikiRepository()
    val query = MutableLiveData<String>()

    fun queryName(name:String) {
        query.value = name
    }

    val hitCount = query.switchMap {
        liveData(Dispatchers.IO) {
            emit(repository.hitCountCheck(it))
        }
    }
}

class MainActivity : AppCompatActivity() {
   private lateinit var  viewModel: MainViewModel
    private val changeObserver =
        Observer<String> {
                value -> value?.let { viewModel.hitCount }
        }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btClick.setOnClickListener {
            viewModel.queryName(tiInput.text.toString())
        }


        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.query.observe(this, changeObserver)
        viewModel.hitCount.observe(
            this,{
                val hitCountString = it.query.searchinfo.totalhits.toString()
                tvShow.text = "Total hits ${hitCountString}"
            }
        )

    }
}