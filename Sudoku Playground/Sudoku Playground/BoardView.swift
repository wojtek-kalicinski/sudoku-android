//
//  BoardView.swift
//  Sudoku Playground
//
//  Created by Wojtek Kaliciński on 3/15/19.
//  Copyright © 2019 google. All rights reserved.
//

import UIKit
import libsudokurenderer

class BoardView: UIView {
    
    let canvas = MultiCanvas()
    var cells: Array<CellView> = Array()
    
    //initWithFrame to init view from code
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }
    
    //initWithCode to init view from xib or storyboard
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }
    
    //common func to init our view
    private func setupView() {
        let cellSize: CGFloat = bounds.width / 9
        
        for i in 0..<SudokuBoardCompanion().board_SIZE {
            let x = SudokuBoardCompanion().getColumn(i: i)
            let y = SudokuBoardCompanion().getRow(i: i)
            let rect = CGRect(x: (CGFloat(x) * cellSize), y: (CGFloat(y) * cellSize), width: cellSize, height: cellSize)
            let centerX: CGFloat = CGFloat(x) * cellSize + cellSize / 2
            let centerY: CGFloat = CGFloat(y) * cellSize + cellSize / 2
            let scale = CGFloat(4)
            var pivotX: CGFloat = cellSize / 2
            var pivotY: CGFloat = cellSize / 2
            
            if (centerX - cellSize / 2 * 4 < 0) {
                pivotX = pivotX + (centerX / 3 - scale / 2 * cellSize / 3)
            } else if (centerX + cellSize / 2 * 4 > bounds.width) {
                pivotX = pivotX + (centerX - bounds.width + scale / 2 * cellSize) / 3
            }
            if (centerY - cellSize / 2 * 4 < 0) {
                pivotY = pivotY + (centerY / 3 - scale / 2 * cellSize / 3)
            } else if (centerY + cellSize / 2 * 4 > bounds.height) {
                pivotY = pivotY + (centerY - bounds.height + scale / 2 * cellSize) / 3
            }
            let cellView = CellView(frame: rect)
            //cellView.layer.anchorPoint = CGPoint(x: pivotX, y: pivotY)
            cells.append(cellView)
            addSubview(cellView)
        }
    }
        
    override func draw(_ rect: CGRect) {
        canvas.grabContext()
        SudokuRendererKt.drawBoard(multiCanvas: canvas, width: Float(bounds.width), height: Float(bounds.height), strokeWidthBold: 2, strokeWidthNormal: 1, colorBold: Int32(bitPattern: 0xFFFF0000), colorNormal: Int32(bitPattern: 0xFF0000FF))
    }
    
    func setBoard(board: SudokuBoard?) {
        for n in 0..<cells.count {
            let cellView = cells[n]
            cellView.setNumbersShown(numbers: board?.possibleValues(pos: Int32(n)))
            cellView.isNumberConfirmed = board?.isCommitedValue(pos: Int32(n)) == true
            cellView.isChangeable = board?.isStartingValue(pos: Int32(n)) == false
        }
    }
}
