package com.example.navegacionjetpack_hmm

import android.os.Bundle
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.navigation.compose.composable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navegacionjetpack_hmm.ui.theme.NavegacionJetpack_HMMTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavegacionJetpack_HMMTheme {
                val navController= rememberNavController()
                val navStackEntry by navController.currentBackStackEntryAsState()
                val canNavigateBack = navStackEntry?.destination?.route != Screen.Home.route
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar={
                        TopAppBar(
                            title = {Text("Juegos")},
                            navigationIcon = {
                                if (canNavigateBack){
                                    IconButton(onClick = {navController.popBackStack()}) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Regresar"
                                        )
                                    }
                                }
                            }
                        )
                    }) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ){
                        composable(route=Screen.Home.route){
                            Home(navController)
                        }
                        composable(route=Screen.Buscaminas.route){
                            Buscaminas(navController)
                        }
                        composable(route=Screen.EncuentraTopo.route){
                            EncuentraTopo(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Home(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { navController.navigate(Screen.Buscaminas.route) }) {
            Text("Ir a Buscamians")
        }
        Button(onClick = { navController.navigate(Screen.EncuentraTopo.route) }) {
            Text("Ir a Encuentra el topo")
        }
    }
}

@Composable
fun Buscaminas(navController: NavController){
    var columnas=7
    var filas=11

    var racha = remember { mutableStateOf(0) }
    var verVictoria = remember { mutableStateOf(false) }

    var estadoBotones = remember {
        List(filas * columnas) { mutableStateOf(true) }
    }
    var minas = remember {
        List(filas * columnas) { mutableStateOf(asignaMina()) }
    }
    var verAlerta = remember {
        mutableStateOf(false)
    }

    if(verAlerta.value){
        AlertDialog(onDismissRequest = {
            verAlerta.value = false
        }, title = { Text(text = "Perdiste") }, text = { Text(text =
            "Encontraste una mina")
        }, confirmButton = {
            Button(onClick = {
                verAlerta.value = false
                estadoBotones.forEach { it.value = true }
                minas.forEach { it.value = asignaMina() }
            }) {
                Text(text = "Reiniciar juego")
            }
        })
        if(verAlerta.value){
            AlertDialog(
                onDismissRequest = {
                    verAlerta.value = false
                },
                title = { Text(text = "Perdiste") },
                text = { Text(text = "Encontraste una mina") },
                confirmButton = {
                    Button(onClick = {
                        verAlerta.value = false
                        racha.value = 0
                        estadoBotones.forEach { it.value = true }
                        minas.forEach { it.value = asignaMina() }
                    }) {
                        Text(text = "Reiniciar juego")
                    }
                }
            )
        }
        if(verVictoria.value){
            AlertDialog(
                onDismissRequest = { verVictoria.value = false },
                title = { Text(text = "¡Ganaste!") },
                text = { Text(text = "Destapaste "+racha.value+" casillas sin minas") },
                confirmButton = {
                    Button(onClick = {
                        verVictoria.value = false
                        racha.value = 0
                        estadoBotones.forEach { it.value = true }
                        minas.forEach { it.value = asignaMina() }
                    }) {
                        Text("Reiniciar juego")
                    }
                }
            )
        }
    }

    Column(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        for(i in 0 until filas){
            Row(modifier= Modifier.fillMaxSize().weight(1f)){
                for(j in 0 until columnas){
                    val index = i*columnas+j
                    Column(modifier= Modifier.fillMaxSize().weight(1f).padding(2.dp)) {
                        Button(onClick = {
                            estadoBotones[index].value=false
                            if(!minas[index].value){
                                racha.value++

                                if(racha.value == 10){
                                    verVictoria.value = true
                                }
                            }
                            else{
                                racha.value = 0
                                verAlerta.value = true
                            }},
                            shape= RectangleShape,
                            modifier= Modifier.fillMaxSize(),
                            enabled = estadoBotones[index].value
                        ){
                            Text(if(!estadoBotones[index].value && minas[index].value)
                                "O" else "X",
                                style = TextStyle(fontSize = 24.sp),
                                color = if (!estadoBotones[index].value && minas[index].value)
                                    Color.Red else Color.Black
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EncuentraTopo(navController: NavController){
    var racha by remember { mutableStateOf(0) }
    var x by remember { mutableStateOf(0.dp) }
    var y by remember { mutableStateOf(0.dp) }
    var gameOver by remember { mutableStateOf(false) }
    var tiempoActivo by remember { mutableStateOf(0) }

    LaunchedEffect(tiempoActivo) {
        delay(1000)
        gameOver = true
    }

    if(!gameOver) {
        Button(
            onClick = {
                x = (0..300).random().dp
                y = (0..600).random().dp
                racha ++
                tiempoActivo++
            },
            modifier = Modifier.offset(x, y)
        ) {
            Text(racha.toString())
        }
    }
    else{
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text("Perdiste tu racha fue de: "+racha.toString())
            Button(
                onClick = {
                    gameOver = false
                    tiempoActivo++
                    racha=0
                }) {
                Text("Reintentar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavegacionJetpack_HMMTheme {
        val navController = rememberNavController()
        EncuentraTopo(navController)
    }
}

fun asignaMina(): Boolean{
    val numeroRandom = java.util.Random().nextInt(10)
    return (numeroRandom>7)
}