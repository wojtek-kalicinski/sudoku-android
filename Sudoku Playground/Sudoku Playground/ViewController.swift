/*
 * Copyright 2019 Google LLC
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

import UIKit
import libsudoku

class ViewController: UIViewController {
    var seed = 1234

    @IBAction func solveClicked(_ sender: UIButton) {
        boardView.setBoard(board: game!.solvedBoard)
    }
    
    @IBAction func generateClicked(_ sender: UIButton) {
        game = getBoard(regen: true, seed: Int64(seed))
        seed = seed + 1
        let board = game!.board
        boardView.setBoard(board: board)
        localBoardSource.game = game
    }
    
    @IBOutlet weak var boardView: BoardView!

    let localBoardSource = LocalBoardSource(storage: MultiStorage())
    let generateBoardSource = GenerateBoardSource()
    var game: SudokuGame? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        game = getBoard(regen: false, seed: Int64(seed))
        let board = game!.board
        boardView.setBoard(board: board)
    }
    
    
    func getBoard(regen: Bool, seed: Int64) -> SudokuGame {
        var game: SudokuGame? = nil
        if (!regen) {
            game = localBoardSource.game
        }

        if (game == nil) {
            game = generateBoardSource.generateBoard(seed: seed)
        }
        return game!
    }
    
    func saveBoard(game: SudokuGame?) {
        localBoardSource.game = game
    }
}

