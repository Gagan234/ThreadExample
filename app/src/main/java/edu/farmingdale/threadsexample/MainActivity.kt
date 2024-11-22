package edu.farmingdale.threadsexample

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.farmingdale.threadsexample.ui.theme.ThreadsExampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThreadsExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NavigationScreen(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf("countdown") }

    when (currentScreen) {
        "countdown" -> {
            Column(modifier) {
                Button(onClick = { currentScreen = "fibonacciNoBg" }) {
                    Text("Fibonacci Without Background Thread")
                }
                Button(onClick = { currentScreen = "fibonacciCoroutine" }) {
                    Text("Fibonacci With Coroutine")
                }
                CountDownActivity()
            }
        }
        "fibonacciNoBg" -> {
            Column(modifier) {
                Button(onClick = { currentScreen = "countdown" }) {
                    Text("Back to Countdown")
                }
                FibonacciDemoNoBgThread()
            }
        }
        "fibonacciCoroutine" -> {
            Column(modifier) {
                Button(onClick = { currentScreen = "countdown" }) {
                    Text("Back to Countdown")
                }
                FibonacciDemoWithCoroutine()
            }
        }
    }
}

@Composable
fun FibonacciDemoNoBgThread() {
    var answer by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("40") }

    Column {
        Row {
            androidx.compose.material3.TextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text("Number?") },
                singleLine = true
            )
            Button(onClick = {
                val num = textInput.toLongOrNull() ?: 0
                val fibNumber = fibonacci(num)
                answer = NumberFormat.getNumberInstance(Locale.US).format(fibNumber)
            }) {
                Text("Fibonacci")
            }
        }
        Text("Result: $answer")
    }
}

@Composable
fun FibonacciDemoWithCoroutine() {
    var answer by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("40") }
    val coroutineScope = rememberCoroutineScope()

    Column {
        Row {
            androidx.compose.material3.TextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text("Number?") },
                singleLine = true
            )
            Button(onClick = {
                coroutineScope.launch(Dispatchers.Default) {
                    val num = textInput.toLongOrNull() ?: 0
                    val fibNumber = fibonacci(num)
                    answer = NumberFormat.getNumberInstance(Locale.US).format(fibNumber)
                }
            }) {
                Text("Fibonacci")
            }
        }
        Text("Result: $answer")
    }
}

@Composable
fun CountDownActivity() {
    val context = LocalContext.current
    var timeLeft by remember { mutableIntStateOf(60) }
    var isRunning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$timeLeft",
            fontSize = 64.sp,
            fontWeight = if (timeLeft <= 10) FontWeight.Bold else FontWeight.Normal,
            color = if (timeLeft <= 10) Color.Red else Color.Black,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = {
            if (!isRunning) {
                isRunning = true
                coroutineScope.launch {
                    while (timeLeft > 0) {
                        delay(1000L)
                        timeLeft -= 1
                    }
                    isRunning = false
                    playSound(context)
                }
            }
        }, enabled = !isRunning) {
            Text("Start Timer")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            timeLeft = 60
            isRunning = false
        }) {
            Text("Reset Timer")
        }
    }
}

fun playSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.timer)
    mediaPlayer.start()

    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}

// ToDo 1: Call `FibonacciDemoNoBgThrd` that calculates the Fibonacci number of a given number.
// ToDo 2: Create a composable function called `FibonacciDemoWithCoroutine` that calculates the
//  Fibonacci number of a given number using a coroutine.
// ToDo 3: Start the application using the CountDownActivity
// ToDo 4: Make the Text of the timer larger
// ToDo 5: Show a visual indicator of the timer going down to 0
// ToDo 6: Add a button to rest the timer
// ToDo 7: Play a sound when the timer reaches 0
// ToDo 8: During the last 10 seconds, make the text red and bold

//test