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
        SudokuRendererKt.drawBoard(multiCanvas: canvas, width: Float(bounds.width), height: Float(bounds.height), strokeWidthBold: 2, strokeWidthNormal: 1, colorBold: Int32(bitPattern: 0xFF212121), colorNormal: Int32(bitPattern: 0xFF757575))
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
