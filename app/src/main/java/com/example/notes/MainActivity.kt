package com.example.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notes.ui.theme.NotesTheme

data class Nota(
    val titulo: String,
    val fecha: String,
    val color: Color
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesTheme {
                Surface  (
                    modifier = Modifier.fillMaxSize()
                ){
                    Notas()
                }
            }
        }
    }
}

@Composable
fun Notas(){
    //var titulo by remember { mutableStateOf( value = "") }
    var Agregar by remember { mutableStateOf( false ) }

    if (Agregar){
        PantallaCrearNota(onBack = { Agregar = false})
    }else{
        PantallaPrincipal( onAddClick = { Agregar = true})
    }
}

@Composable
fun PantallaPrincipal(onAddClick: () -> Unit){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Notas",
                    fontStyle = FontStyle.Italic,
                    fontSize = 24.sp,
                    color = Color.Magenta,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    modifier = Modifier.padding(end = 16.dp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Ordenar"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddClick,
                modifier = Modifier.align (Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar Nota")
            }
        }
    }

@Composable
fun PantallaCrearNota(onBack: ()-> Unit){
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ){
        Text(
            text = "Nueva nota",
            color = Color.Magenta,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Titulo",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        BasicTextField(
            value = titulo,
            onValueChange = { titulo = it },
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White)
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Contenido",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        BasicTextField(
            value = contenido,
            onValueChange = { contenido = it },
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
                .background(Color.White)
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Guardar")
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesTheme {
        Greeting("Android")
    }
}