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

package me.kalicinski.sudoku.di

import android.content.Context
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import me.kalicinski.multiplatform.MultiStorage
import me.kalicinski.sudoku.SudokuApplication
import me.kalicinski.sudoku.datasource.GenerateBoardSource
import me.kalicinski.sudoku.datasource.LocalBoardSource
import javax.inject.Singleton

@Module
abstract class AppModule {
    @Module
    companion object {
        @Provides
        @JvmStatic
        internal fun providesContext(application: SudokuApplication): Context {
            return application.applicationContext
        }

        @Provides
        @JvmStatic
        @Singleton
        internal fun providesStorage(context: Context): MultiStorage {
            return MultiStorage(PreferenceManager.getDefaultSharedPreferences(context.applicationContext))
        }

        @Provides
        @JvmStatic
        @Singleton
        internal fun providesGeneratedBoardSource(): GenerateBoardSource {
            return GenerateBoardSource()
        }

        @Provides
        @JvmStatic
        @Singleton
        internal fun providesLocalBoardSource(storage: MultiStorage): LocalBoardSource {
            return LocalBoardSource(storage)
        }


    }
}