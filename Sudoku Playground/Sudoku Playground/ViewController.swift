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
        seed += 1
        boardRepository.getBoard(regen: true, seed: Int64(seed), callback: {[weak self] newGame in
            guard let self = self else { return KotlinUnit() }
            self.game = newGame
            self.boardView.setBoard(board: self.game!.board)
            self.boardRepository.saveBoard(game: newGame)
            return KotlinUnit()
        })
    }
    
    @IBOutlet weak var boardView: BoardView!

    var game: SudokuGame? = nil
    let boardRepository = BoardRepository(localSource: LocalBoardSource(storage: MultiStorage()), generator: GenerateBoardSource())
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        boardRepository.getBoard(regen: false, seed: Int64(seed), callback: {[weak self] newGame in
            guard let self = self else { return KotlinUnit() }
            self.game = newGame
            self.boardView.setBoard(board: self.game!.board)
            return KotlinUnit()
        })
    }
}

