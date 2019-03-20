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
import libsudokurenderer

class ViewController: UIViewController {

    @IBOutlet weak var boardView: BoardView!
    var storage: MultiStorage = MultiStorage()
    let STORAGE_KEY = "board"
    
    override func viewDidLoad() {
        super.viewDidLoad()
//        // 1
//        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
//            guard let self = self else {
//                return
//            }
//            let pair = SudokuSolver.Companion().generate(listener: nil, random: nil)
//            let board = pair.first as! SudokuBoard
//
//
//            // 2
//            DispatchQueue.main.async { [weak self] in
//                // 3
//                self?.boardView.setBoard(board: board)
//            }
//        }
        let pair = SudokuSolver.Companion().generate(listener: nil, random: nil)
        let board = pair.first as! SudokuBoard
        boardView.setBoard(board: board)
        
        
        let stored = storage.getString(key: STORAGE_KEY)
        if (stored != nil){
            print("stored:", stored!)
        } else {
            let names = ["Ford", "Zaphod", "Trillian", "Arthur", "Marvin"]
            let randomName = names.randomElement()
            print("storing:", randomName!)
            storage.putString(key: STORAGE_KEY, value: randomName)
        }
//        for i:Int32 in 0..<81 {
//            if (board.hasPossibleValue(pos: i)){
//                print(board.getFirstPossibleValue(pos: i), ",", separator: "", terminator:"")
//            } else {
//                print("_,", separator: "", terminator:"")
//            }
//            if (i % 9 == 8) {
//                print()
//            }
//        }
    }
}

