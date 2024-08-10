package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var resultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultado = findViewById(R.id.resultado)
    }

    fun calcular(view: View) {
        val boton = view as Button
        val textoBoton = boton.text.toString()
        val concatenar = resultado.text.toString() + textoBoton
        var mostrar = quitarCeros(concatenar)

        if (textoBoton == "=") {
            try {
                val respuesta = eval(concatenar.dropLast(1)) // Remove the '=' at the end
                mostrar = respuesta.toString()
            } catch (e: Exception) {
                mostrar = "Error"
            }
        }

        resultado.text = mostrar
    }

    fun quitarCeros(str: String): String {
        var i = 0
        while (i < str.length && str[i] == '0') i++
        return str.substring(i)
    }

    // Función para evaluar la expresión matemática
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '

            fun nextChar() {
                ch = if (++pos < str.length) str[pos] else '\u0000'
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: $ch")
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+') -> x += parseTerm() // suma
                        eat('-') -> x -= parseTerm() // resta
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*') -> x *= parseFactor() // multiplicación
                        eat('/') -> x /= parseFactor() // división
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                when {
                    eat('+') -> return parseFactor() // operador unario más
                    eat('-') -> return -parseFactor() // operador unario menos
                }

                var x: Double
                val startPos = pos
                if (eat('(')) { // paréntesis
                    x = parseExpression()
                    eat(')')
                } else if (ch in '0'..'9' || ch == '.') { // números
                    while (ch in '0'..'9' || ch == '.') nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: $ch")
                }
                return x
            }
        }.parse()
    }
}
