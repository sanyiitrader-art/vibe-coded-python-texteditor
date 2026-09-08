package com.pyth.editor.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.pyth.editor.core.PythonLexer

class CodeEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var onTextChangeListener: ((String, Int) -> Unit)? = null
    var onSelectionChangeListener: ((Int) -> Unit)? = null

    init {
        setHorizontallyScrolling(true)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        isVerticalScrollBarEnabled = true
        isHorizontalScrollBarEnabled = true
        
        addTextChangedListener(object : TextWatcher {
            private var beforeText = ""
            private var start = 0
            private var count = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeText = s?.toString() ?: ""
                this.start = start
                this.count = count
            }
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Smart brackets and quotes
                if (count == 1 && s != null && start < s.length) {
                    val c = s[start]
                    val pairs = mapOf('(' to ')', '[' to ']', '{' to '}', '"' to '"', '\'' to '\'')
                    if (c in pairs.keys) {
                        val closing = pairs[c]!!
                        text?.insert(start + 1, closing.toString())
                        setSelection(start + 1)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s == null) return
                val text = s.toString()
                
                // Smart indentation on Enter
                if (count == 1 && start < text.length && text[start] == '\n') {
                    val lineStart = text.lastIndexOf('\n', start - 1) + 1
                    val previousLine = text.substring(lineStart, start)
                    val indentation = previousLine.takeWhile { it == ' ' }
                    var newIndent = indentation
                    if (previousLine.trimEnd().endsWith(':')) {
                        newIndent += "    "
                    }
                    if (newIndent.isNotEmpty()) {
                        text?.insert(start + 1, newIndent)
                        setSelection(start + 1 + newIndent.length)
                    }
                }

                onTextChangeListener?.invoke(text, selectionStart)
                onSelectionChangeListener?.invoke(selectionStart)
                highlightSyntax(s)
            }
        })
    }

    private fun highlightSyntax(editable: Editable) {
        val text = editable.toString()
        val tokens = PythonLexer.tokenize(text)
        
        editable.getSpans(0, text.length, android.text.style.ForegroundColorSpan::class.java).forEach {
            editable.removeSpan(it)
        }
        
        tokens.forEach { token ->
            editable.setSpan(
                android.text.style.ForegroundColorSpan(token.color),
                token.start,
                token.end,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

class GutterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF78857F.toInt()
        textSize = 36f
        textAlign = Paint.Align.RIGHT
    }
    private val bgPaint = Paint().apply { color = 0xFF0D0F10.toInt() }
    
    var lineCount: Int = 1
    var scrollY: Int = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        val lineHeight = paint.fontMetrics.bottom - paint.fontMetrics.top
        val padRight = 24f
        for (i in 1..lineCount) {
            val y = i * lineHeight - scrollY
            if (y > 0 && y < height + lineHeight) {
                canvas.drawText(i.toString(), width - padRight, y, paint)
            }
        }
    }
}

class CodeEditorLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {
    private val gutter = GutterView(context)
    private val scrollView = HorizontalScrollView(context)
    private val editText = CodeEditText(context)
    var onCodeChanged: ((String, Int) -> Unit)? = null

    init {
        addView(gutter)
        addView(scrollView)
        scrollView.addView(editText)
        editText.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            gutter.scrollY = scrollY
            gutter.invalidate()
        }
        editText.onTextChangeListener = { text, cursor ->
            gutter.lineCount = text.count { it == '\n' } + 1
            gutter.invalidate()
            onCodeChanged?.invoke(text, cursor)
        }
        setWillNotDraw(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val gutterWidth = 120 
        measureChild(gutter, MeasureSpec.makeMeasureSpec(gutterWidth, MeasureSpec.EXACTLY), heightMeasureSpec)
        measureChild(scrollView, MeasureSpec.makeMeasureSpec(width - gutterWidth, MeasureSpec.EXACTLY), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val gutterWidth = 120
        gutter.layout(0, 0, gutterWidth, b - t)
        scrollView.layout(gutterWidth, 0, r - l, b - t)
    }

    fun setText(text: String) {
        editText.setText(text)
        editText.setSelection(text.length)
    }

    fun getText(): String {
        return editText.text.toString()
    }
    
    fun insertText(text: String) {
        editText.text.insert(editText.selectionStart, text)
    }
}

@Composable
fun CodeEditor(
    modifier: Modifier = Modifier,
    initialText: String,
    onTextChanged: (String, Int) -> Unit
) {
    val context = LocalContext.current
    val editorView = remember { CodeEditorLayout(context) }
    AndroidView(
        modifier = modifier,
        factory = {
            editorView.onCodeChanged = onTextChanged
            editorView
        },
        update = { view ->
            if (view.getText() != initialText) {
                view.setText(initialText)
            }
        }
    )
}