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
        boardViewModel = ViewModelProviders.of(this, viewModelFactory).get(BoardViewModel::class.java)
        val seed = intent?.data?.lastPathSegment?.toLongOrNull()
        intent = null
        if (seed == null) {
            boardViewModel.initIfEmpty()
        } else {
            boardViewModel.generateNewBoard(true, seed)
        }

        with(DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)) {
            setLifecycleOwner(this@MainActivity)
            boardvm = boardViewModel
        }
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
}
