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

class CellView: UIView {
    
    private var canvas = MultiCanvas()
    
    var numbersShowing: Set<KotlinInt> = [KotlinInt(int: Int32(2))]
    var isNumberConfirmed = true {
        didSet {
            setNeedsDisplay()
        }
    }
    
    private var isNumberIncorrect = false {
        didSet {
            setNeedsDisplay()
        }
    }

    private var bigNumberHeight: CGFloat = 0
    private var smallNumberHeight: CGFloat = 0
    var isChangeable = false {
        didSet {
            //setOnClickListener(if (isChangeable) clickListener else null)

            setNeedsDisplay()
        }
    }
    
    func setNumbersShown(numbers: Array<KotlinInt>?) {
        numbersShowing.removeAll(keepingCapacity: true)
        if numbers != nil {
            for n in numbers! {
                numbersShowing.insert(n)
            }
        }
        isNumberConfirmed = false
        isNumberIncorrect = false
        setNeedsDisplay()
    }

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
        isOpaque = false
        bigNumberHeight = 0.8 * bounds.height
        smallNumberHeight = 0.2 * bounds.height
    }
    
    override func draw(_ rect: CGRect) {
        canvas.grabContext()
        SudokuRendererKt.drawCell(multiCanvas: canvas, numbersShowing: numbersShowing, width: Float(bounds.width), height: Float(bounds.height), isFocused: isFocused, isChangeable: isChangeable, isNumberIncorrect: isNumberIncorrect, isNumberConfirmed: isNumberConfirmed, colorFocused: Int32(bitPattern: 0xFFe6e6e6), colorChangeable: Int32(bitPattern: 0xFF777777), colorIncorrect: Int32(bitPattern: 0xFFbb0000), colorHighlight: Int32(bitPattern: 0xFFFFECB3), bigNumberHeight: Float(bigNumberHeight), smallNumberHeight: Float(smallNumberHeight))
    }

}
