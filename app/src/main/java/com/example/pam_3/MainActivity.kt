package com.example.pam_3

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Stack

class MainActivity : AppCompatActivity() {
    private lateinit var displayTextView: TextView
    private lateinit var placeholderTextView: TextView
    private var expression = StringBuilder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi TextView
        displayTextView = findViewById(R.id.answer)
        placeholderTextView = findViewById(R.id.placeholder)

        // Setup onClickListeners untuk tombol
        setNumberButtonListeners()
        setActionButtonListeners()

    }

    // Fungsi untuk menetapkan onClickListeners untuk tombol angka
    private fun setNumberButtonListeners() {
        val buttons = listOf(
            Pair(R.id.num0, "0"),
            Pair(R.id.num1, "1"),
            Pair(R.id.num2, "2"),
            Pair(R.id.num3, "3"),
            Pair(R.id.num4, "4"),
            Pair(R.id.num5, "5"),
            Pair(R.id.num6, "6"),
            Pair(R.id.num7, "7"),
            Pair(R.id.num8, "8"),
            Pair(R.id.num9, "9"),
            Pair(R.id.numDot, ".")
        )

        for (button in buttons) {
            findViewById<TextView>(button.first).setOnClickListener {
                appendToExpression(button.second)
            }
        }
    }

    // Fungsi untuk menetapkan onClickListeners untuk tombol aksi
    private fun setActionButtonListeners() {
        val actions = listOf(
            Pair(R.id.actionAdd, "+"),
            Pair(R.id.actionMinus, "-"),
            Pair(R.id.actionMultiply, "*"),
            Pair(R.id.actionDivide, "/"),
            Pair(R.id.startBracket, "("),
            Pair(R.id.closeBracket, ")")
        )

        for (action in actions) {
            findViewById<TextView>(action.first).setOnClickListener {
                appendToExpression(action.second)
            }
        }

        // Tombol Equals (=)
        findViewById<TextView>(R.id.actionEquals).setOnClickListener {
            calculateResult()
        }

        // Tombol Clear (CE)
        findViewById<TextView>(R.id.clear).setOnClickListener {
            clearExpression()
        }

        // Tombol Back
        findViewById<TextView>(R.id.actionBack).setOnClickListener {
            removeLastCharacter()
        }
    }

    // Fungsi untuk menambahkan input ke ekspresi
    private fun appendToExpression(input: String) {
        expression.append(input)
        placeholderTextView.text = expression.toString()
    }

    // Fungsi untuk menghitung hasil ekspresi
    private fun calculateResult() {
        try {
            val infix = expression.toString()
            val postfix = infixToPostfix(infix)
            val result = evaluatePostfix(postfix)
            displayTextView.text = result.toString()
        } catch (e: Exception) {
            displayTextView.text = "Error"
        }
    }

    // Fungsi untuk membersihkan ekspresi
    private fun clearExpression() {
        expression.clear()
        placeholderTextView.text = ""
        displayTextView.text = ""
    }

    // Fungsi untuk menghapus karakter terakhir dari ekspresi
    private fun removeLastCharacter() {
        if (expression.isNotEmpty()) {
            expression.deleteCharAt(expression.length - 1)
            placeholderTextView.text = expression.toString()
        }
    }

    // Fungsi untuk mengubah infix menjadi postfix
    private fun infixToPostfix(infix: String): List<String> {
        val result = mutableListOf<String>()
        val stack = Stack<Char>()
        var i = 0

        while (i < infix.length) {
            val c = infix[i]

            when {
                c.isDigit() || c == '.' -> {  // Angka atau titik desimal
                    var number = "$c"
                    while (i + 1 < infix.length && (infix[i + 1].isDigit() || infix[i + 1] == '.')) {
                        i++
                        number += infix[i]
                    }
                    result.add(number)
                }
                c == '(' -> stack.push(c)
                c == ')' -> {
                    while (stack.isNotEmpty() && stack.peek() != '(') {
                        result.add(stack.pop().toString())
                    }
                    stack.pop()  // Buang '(' dari stack
                }
                isOperator(c) -> {
                    while (stack.isNotEmpty() && precedence(c) <= precedence(stack.peek())) {
                        result.add(stack.pop().toString())
                    }
                    stack.push(c)
                }
            }
            i++
        }

        // Keluarkan operator yang tersisa di stack
        while (stack.isNotEmpty()) {
            result.add(stack.pop().toString())
        }

        return result
    }

    // Fungsi untuk mengevaluasi postfix
    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = Stack<Double>()

        for (token in postfix) {
            if (token.toDoubleOrNull() != null) {
                stack.push(token.toDouble())
            } else if (isOperator(token[0])) {
                val b = stack.pop()
                val a = stack.pop()
                val result = when (token[0]) {
                    '+' -> a + b
                    '-' -> a - b
                    '*' -> a * b
                    '/' -> a / b
                    else -> throw IllegalArgumentException("Unknown operator: ${token[0]}")
                }
                stack.push(result)
            }
        }

        return stack.pop()
    }

    // Fungsi untuk menentukan apakah karakter adalah operator
    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '*' || c == '/'
    }

    // Fungsi untuk menentukan prioritas operator
    private fun precedence(c: Char): Int {
        return when (c) {
            '+', '-' -> 1
            '*', '/' -> 2
            else -> -1
        }
    }
}
