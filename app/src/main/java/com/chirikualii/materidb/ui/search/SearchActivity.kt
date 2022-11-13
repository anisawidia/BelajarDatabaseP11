package com.chirikualii.materidb.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.chirikualii.materidb.R
import com.chirikualii.materidb.data.model.Movie
import com.chirikualii.materidb.databinding.ActivitySearchBinding
import com.chirikualii.materidb.ui.adapter.MovieListAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var listAdapter: MovieListAdapter

    var queryFlow = MutableStateFlow("")
    val viewModel : SearchViewModel by viewModels<SearchViewModel>(
        factoryProducer = { SearchViewModelFactory(context = this)}
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeView()
        listAdapter = MovieListAdapter()
        binding.rvMovie.adapter = listAdapter

        binding.etSearch.doOnTextChanged { text, _,_, _ ->

            lifecycleScope.launch{
                queryFlow.emit(text.toString())
                queryFlow
                    .debounce(500)
                    .collect{
                        viewModel.doSearchMovie(it)
                        Toast.makeText(this@SearchActivity, "searching ${it}", Toast.LENGTH_SHORT).show()
                    }
            }

            viewModel.doSearchMovie(text.toString())
            Toast.makeText(this,"searching ${text}", Toast.LENGTH_SHORT).show()

        }

    }

    private fun observeView() {
        viewModel.listMovie.observe(this){
            listAdapter.addItem(it)
        }

        viewModel.isLoading.observe(this){
            if(it){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }
}