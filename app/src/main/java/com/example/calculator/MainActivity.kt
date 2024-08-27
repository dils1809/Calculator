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

    // Función para manejar la calculadora
    fun calcular(view: View) {
        val boton = view as Button
        val textoBoton = boton.text.toString()

        // Si el botón es "Reset", limpia el campo de resultado
        if (textoBoton == "Reset") {
            resultado.text = "0" // Resetea la pantalla pero no cierra el programa
            return
        }

        // Concatenar el nuevo valor presionado, pero evitando concatenar al valor "0" inicial
        val concatenar = if (resultado.text.toString() == "0") textoBoton else resultado.text.toString() + textoBoton
        var mostrar = quitarCeros(concatenar)

        if (textoBoton == "=") {
            try {
                val respuesta = eval(concatenar.dropLast(1)) // Remueve el '=' al final
                mostrar = respuesta.toString()
            } catch (e: Exception) {
                mostrar = "Error"
            }
        }

        resultado.text = mostrar
    }

    // Función para eliminar ceros iniciales innecesarios
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
                        eat('+') -> x += parseTerm() // Suma
                        eat('-') -> x -= parseTerm() // Resta
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*') -> x *= parseFactor() // Multiplicación
                        eat('/') -> x /= parseFactor() // División
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                when {
                    eat('+') -> return parseFactor() // Operador unario más
                    eat('-') -> return -parseFactor() // Operador unario menos
                }

                var x: Double
                val startPos = pos
                if (eat('(')) { // Paréntesis
                    x = parseExpression()
                    eat(')')
                } else if (ch in '0'..'9' || ch == '.') { // Números
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

