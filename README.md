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
 * The sudoku generator/solver is a pure Kotlin common module, included from the Android
   and JavaScript frontends
 * Uses [Architecture Components](https://developer.android.com/topic/libraries/architecture/):
 ViewModels and LiveData
 * Uses [Data Binding](https://developer.android.com/topic/libraries/data-binding/index.html) to
 connect views to ViewModels (and observes LiveData!)
 * Uses [dagger-android](https://google.github.io/dagger/android.html) for dependency injection
 * Uses [Android App Links](https://developer.android.com/training/app-links/index.html) to open 
 deep links directly into the app, and also provides an 
 [Instant App](https://developer.android.com/topic/instant-apps/index.html) 
 version from the same project
 
 To showcase the power of Kotlin multiplatform, some additional considerations were made:
 * Sudoku boards are generated using a deterministic algorithm shared between frontends, i.e.
 given a pseudo-random number generator (PRNG) initialized with the same seed, 
 you will always get the same board
 * The project contains a seeded PRNG written in Kotlin common (since Kotlin doesn't provide a 
 built-in PRNG for Kotlin/JS in the standard library)
 * Android app supports sharing and opening deep links containing a seed number 
 used to initialize the board (e.g. https://sudokuplayground.firebaseapp.com/sudoku/1234)
 * The website counterpart to the Android app is hosted under the same URLs and will show the 
 same board when opened in a web browser, thanks to the shared, deterministic algorithm
 
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
