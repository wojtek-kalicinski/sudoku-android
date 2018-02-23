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

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.instantapps.InstantApps
import dagger.android.AndroidInjection
import me.kalicinski.sudoku.basefeature.R
import me.kalicinski.sudoku.basefeature.databinding.ActivityMainBinding
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private lateinit var boardViewModel: BoardViewModel
    @Inject lateinit var viewModelFactory: SudokuViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState)

        val seed = intent?.data?.lastPathSegment?.toLongOrNull()
        intent = null

        boardViewModel = ViewModelProviders.of(this, viewModelFactory).get(BoardViewModel::class.java)
        if (seed == null) {
            boardViewModel.initIfEmpty()
        } else {
            boardViewModel.generateNewBoard(true, seed)
        }

        with(DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)) {
            setLifecycleOwner(this@MainActivity)
            boardvm = boardViewModel
            setSupportActionBar(toolbar)
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(newIntent)
        val seed = newIntent?.data?.lastPathSegment?.toLongOrNull()
        intent = null
        if (seed != null) {
            boardViewModel.generateNewBoard(true, seed)
        }
    }

    override fun onStop() {
        super.onStop()
        boardViewModel.saveNow()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        if (InstantApps.isInstantApp(this)){
            menuInflater.inflate(R.menu.instant, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.share -> {
                with(Intent()) {
                    setAction(Intent.ACTION_SEND)
                    putExtra(
                            Intent.EXTRA_TEXT,
                            "https://sudokuplayground.firebaseapp.com/sudoku/${boardViewModel.seed}"
                    );
                    setType("text/plain")
                    startActivity(Intent.createChooser(this, getString(R.string.send_to)));
                }
                return true
            }
            R.id.get_app -> {
                InstantApps.showInstallPrompt(
                        this,
                        Intent().apply {
                            component = this@MainActivity.componentName
                        },
                        REQUEST_INSTALL,
                        REFERRER
                )
                return true
            }
            R.id.oss_info -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
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
