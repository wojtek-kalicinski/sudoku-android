/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package me.kalicinski.sudoku

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.instantapps.InstantApps
import dagger.android.support.AndroidSupportInjection
import me.kalicinski.sudoku.databinding.FragmentMainBinding
import javax.inject.Inject


class MainFragment : Fragment() {

    private lateinit var boardViewModel: BoardViewModel
    @Inject lateinit var viewModelFactory: SudokuViewModelFactory
    val mainFragmentArgs: MainFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        boardViewModel = ViewModelProviders.of(this, viewModelFactory).get(BoardViewModel::class.java)

        mainFragmentArgs.seed.takeUnless { it == 0L }.let { seed ->
            if (seed == null) {
                boardViewModel.initIfEmpty()
            } else {
                boardViewModel.generateNewBoard(true, seed)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentMainBinding>(
                inflater,
                R.layout.fragment_main,
                container,
                false
        ).run {
            setLifecycleOwner(this@MainFragment)
            boardvm = boardViewModel
            (activity as? AppCompatActivity)?.let {
                it.setSupportActionBar(toolbar)
                it.supportActionBar?.setDisplayShowTitleEnabled(false)
            }
            root
        }
    }

    override fun onStop() {
        super.onStop()
        boardViewModel.saveNow()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)
        context?.let {
            if (InstantApps.isInstantApp(it)){
                inflater.inflate(R.menu.instant, menu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.share -> {
                with(Intent()) {
                    setAction(Intent.ACTION_SEND)
                    putExtra(
                            Intent.EXTRA_TEXT,
                            "https://sudokuplayground.firebaseapp.com/sudoku/${boardViewModel.game?.seed}"
                    );
                    setType("text/plain")
                    startActivity(Intent.createChooser(this, getString(R.string.send_to)));
                }
                return true
            }
            R.id.get_app -> {
                activity?.let {
                    InstantApps.showInstallPrompt(
                            it,
                            Intent().apply {
                                component = it.componentName
                            },
                            REQUEST_INSTALL,
                            REFERRER
                    )
                }
                return true
            }
            R.id.oss_info -> {
                NavHostFragment.findNavController(this).navigate(R.id.showOssScreen)
                return true
            }
            else -> return false
        }
    }

    companion object {
        private val REFERRER = "Instant App"
        private val REQUEST_INSTALL = 1
    }
}
