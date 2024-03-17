package com.daw.tarea2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daw.tarea2.ui.theme.Tarea2Theme

class MainActivity : ComponentActivity() {
    private val databaseHelper by lazy { DatabaseHelper(this) }

    @SuppressLint("UnusedMaterial3Resource", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tarea2Theme {
                Scaffold {
                    PantallaPrincipal(databaseHelper)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PantallaPrincipal(databaseHelper: DatabaseHelper) {
    var contactos by remember { mutableStateOf(emptyList<Contacto>()) }
    var mostrarContactosDialog by remember { mutableStateOf(false) }
    var contactoConsultado by remember { mutableStateOf<Contacto?>(null) }
    var mostrarContactoMovilDialog by remember { mutableStateOf(false) }
    var contactoMovilConsultado by remember { mutableStateOf<Contacto?>(null) }

    LaunchedEffect(Unit) {
        contactos = databaseHelper.obtenerContactos()
    }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                BotonVisualizarContactos {
                    contactos = databaseHelper.obtenerContactos()
                    mostrarContactosDialog = true
                }
                Spacer(modifier = Modifier.height(16.dp))

                BotonAgregarContacto { contacto ->
                    databaseHelper.agregarContacto(contacto)
                    contactos = databaseHelper.obtenerContactos()
                }
                Spacer(modifier = Modifier.height(16.dp))

                BotonConsultarPorNick { nick ->
                    val contacto = databaseHelper.obtenerContactoPorNick(nick)
                    if (contacto != null) {
                        contactoConsultado = contacto
                        mostrarContactosDialog = true
                    } else {
                        Log.d("Consulta", "Contacto no encontrado")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                BotonConsultarPorMovil { movil ->
                    val contacto = databaseHelper.obtenerContactoPorMovil(movil)
                    if (contacto != null) {
                        contactoMovilConsultado = contacto
                        mostrarContactoMovilDialog = true
                    } else {
                        Log.d("Consulta", "Contacto no encontrado")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                BotonEliminarContactoPorNick { nickEliminar ->
                    val filasEliminadas = databaseHelper.eliminarContactoPorNick(nickEliminar)
                    if (filasEliminadas > 0) {
                        Log.d("Eliminar", "Contacto eliminado correctamente")
                    } else {
                        Log.d("Eliminar", "No se pudo eliminar el contacto")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                BotonEditarMovilEmail { nick, nuevoMovil, nuevoEmail ->
                    val contactoAEditar = databaseHelper.obtenerContactoPorNick(nick)
                    val filasActualizadas = contactoAEditar?.let { it1 ->
                        databaseHelper.editarMovilYEmail(
                            it1, nuevoMovil, nuevoEmail)
                    }
                    if (filasActualizadas != null) {
                        if (filasActualizadas > 0) {
                            Log.d("Editar", "Contacto editado correctamente")
                        } else {
                            Log.d("Editar", "No se pudo editar el contacto")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )

    if (mostrarContactosDialog) {
        if (contactoConsultado != null) {
            ContactoDialog(contactoConsultado!!) {
                mostrarContactosDialog = false
                contactoConsultado = null
            }
        } else {
            ContactosDialog(contactos = contactos) {
                mostrarContactosDialog = false
            }
        }
    }

    if (mostrarContactoMovilDialog) {
        if (contactoMovilConsultado != null) {
            ContactoDialog(contactoMovilConsultado!!) {
                mostrarContactoMovilDialog = false
                contactoMovilConsultado = null
            }
        }
    }
}

@Composable
fun BotonVisualizarContactos(onVisualizarContactos: () -> Unit) {
    Button(
        onClick = {
            onVisualizarContactos()
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Visualizar Contactos")
    }
}

@Composable
fun ContactosDialog(contactos: List<Contacto>, onClose: () -> Unit) {
    if (contactos.isEmpty()) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(text = "Lista de Contactos") },
            text = {
                Text("No hay contactos disponibles.")
            },
            confirmButton = {
                Button(onClick = onClose) {
                    Text(text = "Cerrar")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(text = "Lista de Contactos") },
            text = {
                LazyColumn {
                    items(contactos) { contacto ->
                        ContactoItem(contacto = contacto)
                        Divider()
                    }
                }
            },
            confirmButton = {
                Button(onClick = onClose) {
                    Text(text = "Cerrar")
                }
            }
        )
    }
}

@Composable
fun ContactoDialog(contacto: Contacto?, onClose: () -> Unit) {
    if (contacto != null) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(text = "Información del Contacto") },
            text = {
                Column {
                    Text(text = "Nick: ${contacto.nick}")
                    Text(text = "Móvil: ${contacto.movil}")
                    Text(text = "Apellido1: ${contacto.apellido1}")
                    Text(text = "Apellido2: ${contacto.apellido2}")
                    Text(text = "Nombre: ${contacto.nombre}")
                    Text(text = "Email: ${contacto.email}")
                }
            },
            confirmButton = {
                Button(onClick = onClose) {
                    Text(text = "Cerrar")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(text = "Información del Contacto") },
            text = {
                Text("No se encontró información para el contacto.")
            },
            confirmButton = {
                Button(onClick = onClose) {
                    Text(text = "Cerrar")
                }
            }
        )
    }
}

@Composable
fun ContactoItem(contacto: Contacto) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Nick: ${contacto.nick}", overflow = TextOverflow.Ellipsis, maxLines = 1)
        Text(text = "Móvil: ${contacto.movil}", overflow = TextOverflow.Ellipsis, maxLines = 1)
        Text(text = "Apellido1: ${contacto.apellido1}", overflow = TextOverflow.Ellipsis, maxLines = 1)
        Text(text = "Apellido2: ${contacto.apellido2}", overflow = TextOverflow.Ellipsis, maxLines = 1)
        Text(text = "Nombre: ${contacto.nombre}", overflow = TextOverflow.Ellipsis, maxLines = 1)
        Text(text = "Email: ${contacto.email}", overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
}

@Composable
fun BotonAgregarContacto(onAgregarContacto: (Contacto) -> Unit) {
    val dialogOpen = remember { mutableStateOf(false) }
    val onCloseDialog = { dialogOpen.value = false }

    Button(
        onClick = { dialogOpen.value = true },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Agregar Contacto")
    }

    if (dialogOpen.value) {
        AgregarContactoDialog(
            onDismiss = onCloseDialog,
            onConfirm = { contacto ->
                onAgregarContacto(contacto)
                onCloseDialog()
            }
        )
    }
}

@Composable
fun AgregarContactoDialog(onDismiss: () -> Unit, onConfirm: (Contacto) -> Unit) {
    var nick by remember { mutableStateOf("") }
    var movil by remember { mutableStateOf("") }
    var apellido1 by remember { mutableStateOf("") }
    var apellido2 by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Agregar Contacto") },
        text = {
            Column {
                TextField(
                    value = nick,
                    onValueChange = { nick = it },
                    label = { Text("Nick") }
                )
                TextField(
                    value = movil,
                    onValueChange = { movil = it },
                    label = { Text("Móvil") }
                )
                TextField(
                    value = apellido1,
                    onValueChange = { apellido1 = it },
                    label = { Text("Primer Apellido") }
                )
                TextField(
                    value = apellido2,
                    onValueChange = { apellido2 = it },
                    label = { Text("Segundo Apellido") }
                )
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") }
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val contacto = Contacto(
                        nick = nick,
                        movil = movil,
                        apellido1 = apellido1,
                        apellido2 = apellido2,
                        nombre = nombre,
                        email = email
                    )
                    onConfirm(contacto)
                    onDismiss()
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun BotonConsultarPorNick(onConsultaPorNick: (String) -> Unit) {
    val dialogOpen = remember { mutableStateOf(false) }
    val onCloseDialog = { dialogOpen.value = false }

    Button(
        onClick = { dialogOpen.value = true },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Consultar por Nick")
    }

    if (dialogOpen.value) {
        ConsultarPorNickDialog(
            onDismiss = onCloseDialog,
            onConfirm = { nick ->
                onConsultaPorNick(nick)
                onCloseDialog()
            }
        )
    }
}

@Composable
fun ConsultarPorNickDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var nick by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Consultar por Nick") },
        text = {
            Column {
                TextField(
                    value = nick,
                    onValueChange = { nick = it },
                    label = { Text("Nick") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(nick)
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun BotonConsultarPorMovil(onConsultaPorMovil: (String) -> Unit) {
    val dialogOpen = remember { mutableStateOf(false) }
    val onCloseDialog = { dialogOpen.value = false }

    Button(
        onClick = { dialogOpen.value = true },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Consultar por Móvil")
    }

    if (dialogOpen.value) {
        ConsultarPorMovilDialog(
            onDismiss = onCloseDialog,
            onConfirm = { movil ->
                onConsultaPorMovil(movil)
                onCloseDialog()
            }
        )
    }
}

@Composable
fun ConsultarPorMovilDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var movil by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Consultar por Móvil") },
        text = {
            Column {
                TextField(
                    value = movil,
                    onValueChange = { movil = it },
                    label = { Text("Móvil") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(movil)
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun BotonEliminarContactoPorNick(onEliminarContactoPorNick: (String) -> Unit) {
    val dialogOpen = remember { mutableStateOf(false) }
    val onCloseDialog = { dialogOpen.value = false }

    Button(
        onClick = { dialogOpen.value = true },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Eliminar Contacto por Nick")
    }

    if (dialogOpen.value) {
        EliminarPorNickDialog(
            onDismiss = onCloseDialog,
            onConfirm = { nickEliminar ->
                onEliminarContactoPorNick(nickEliminar)
                onCloseDialog()
            }
        )
    }
}

@Composable
fun EliminarPorNickDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var nickEliminar by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Eliminar por Nick") },
        text = {
            Column {
                TextField(
                    value = nickEliminar,
                    onValueChange = { nickEliminar = it },
                    label = { Text("Nick a eliminar") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(nickEliminar)
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun BotonEditarMovilEmail(onEditarMovilEmail: (String, String, String) -> Unit) {
    val dialogOpen = remember { mutableStateOf(false) }
    val onCloseDialog = { dialogOpen.value = false }

    Button(
        onClick = { dialogOpen.value = true },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Editar Móvil y Email")
    }

    if (dialogOpen.value) {
        EditarMovilEmailDialog(
            onDismiss = onCloseDialog,
            onConfirm = { nickConsulta, nuevoMovil, nuevoEmail ->
                onEditarMovilEmail(nickConsulta, nuevoMovil, nuevoEmail)
                onCloseDialog()
            }
        )
    }
}

@Composable
fun EditarMovilEmailDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var nickConsulta by remember { mutableStateOf("") }
    var nuevoMovil by remember { mutableStateOf("") }
    var nuevoEmail by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Editar Móvil y Email") },
        text = {
            Column {
                TextField(
                    value = nickConsulta,
                    onValueChange = { nickConsulta = it },
                    label = { Text("Nick a editar") }
                )
                TextField(
                    value = nuevoMovil,
                    onValueChange = { nuevoMovil = it },
                    label = { Text("Nuevo móvil") }
                )
                TextField(
                    value = nuevoEmail,
                    onValueChange = { nuevoEmail = it },
                    label = { Text("Nuevo correo electrónico") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(nickConsulta, nuevoMovil, nuevoEmail)
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar")
            }
        }
    )
}