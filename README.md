# Sudoku Playground #️⃣  (work-in-progress sample 👷)

**This is not an official Google product**

Sudoku Playground is a **work-in-progress** sudoku game app for Android and Web (JS).

It's a hobby project to learn about Android APIs, libraries and best practices. It contains a
functional sudoku board generator and solver, as well as UI so that you can actually play a
sudoku board from start to finish. 

At the same time, this app is not meant to showcase the best-in-class sudoku algorithms - 
the ones implemented are _good enough_ for this sample.

## Android development

Some tech you can find inside:

 * Entirely written in [Kotlin](https://kotlinlang.org/)
 * The sudoku generator/solver is a pure kotlin common module, included from the Android
 and JavaScript modules
 * Uses [Architecture Components](https://developer.android.com/topic/libraries/architecture/):
 ViewModels and LiveData
 * Uses [Data Binding](https://developer.android.com/topic/libraries/data-binding/index.html) to
 connect views to viewmodels
 * Uses [dagger-android](https://google.github.io/dagger/android.html) for dependency injection

## Development setup

You require the latest Android Studio 3.1 (or newer) to be able to build the app.

## Contributions

If you've found an error in this sample, please file an issue.

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request. Since this project is still in its very early stages,
if your change is substantial, please raise an issue first to discuss it.

## License

```
Copyright 2018 Google LLC

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
