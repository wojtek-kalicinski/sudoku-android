//
//  ViewController.swift
//  Sudoku Playground
//
//  Created by Wojtek Kaliciński on 1/28/19.
//  Copyright © 2019 google. All rights reserved.
//

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

