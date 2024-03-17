package com.daw.tarea2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CONTACTOS_TABLE = ("CREATE TABLE " + TABLE_CONTACTOS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NICK + " TEXT,"
                + KEY_MOVIL + " TEXT," + KEY_APELLIDO1 + " TEXT,"
                + KEY_APELLIDO2 + " TEXT," + KEY_NOMBRE + " TEXT,"
                + KEY_EMAIL + " TEXT" + ")")
        db.execSQL(CREATE_CONTACTOS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTOS")
        onCreate(db)
    }

    fun agregarContacto(contacto: Contacto): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NICK, contacto.nick)
        values.put(KEY_MOVIL, contacto.movil)
        values.put(KEY_APELLIDO1, contacto.apellido1)
        values.put(KEY_APELLIDO2, contacto.apellido2)
        values.put(KEY_NOMBRE, contacto.nombre)
        values.put(KEY_EMAIL, contacto.email)
        return db.insert(TABLE_CONTACTOS, null, values)
    }

    // Visualizar los contactos
    @SuppressLint("Range")
    fun obtenerContactos(): List<Contacto> {
        val contactos = mutableListOf<Contacto>()
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_CONTACTOS,
            arrayOf(KEY_ID, KEY_NICK, KEY_MOVIL, KEY_APELLIDO1, KEY_APELLIDO2, KEY_NOMBRE, KEY_EMAIL),
            null,
            null,
            null,
            null,
            null
        )
        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val contacto = Contacto(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_NICK)),
                    cursor.getString(cursor.getColumnIndex(KEY_MOVIL)),
                    cursor.getString(cursor.getColumnIndex(KEY_APELLIDO1)),
                    cursor.getString(cursor.getColumnIndex(KEY_APELLIDO2)),
                    cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)),
                    cursor.getString(cursor.getColumnIndex(KEY_EMAIL))
                )
                contactos.add(contacto)
            }
        }
        return contactos
    }

    // Consultar contacto por su nick
    @SuppressLint("Range")
    fun obtenerContactoPorNick(nick: String): Contacto? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_CONTACTOS, arrayOf(KEY_ID, KEY_NICK, KEY_MOVIL, KEY_APELLIDO1, KEY_APELLIDO2, KEY_NOMBRE, KEY_EMAIL),
            "$KEY_NICK=?", arrayOf(nick), null, null, null, null)
        return if (cursor.moveToFirst()) {
            val contacto = Contacto(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NICK)),
                cursor.getString(cursor.getColumnIndex(KEY_MOVIL)),
                cursor.getString(cursor.getColumnIndex(KEY_APELLIDO1)),
                cursor.getString(cursor.getColumnIndex(KEY_APELLIDO2)),
                cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)),
                cursor.getString(cursor.getColumnIndex(KEY_EMAIL))
            )
            cursor.close()
            contacto
        } else {
            null
        }
    }

    // Consultar contacto por su móvil
    @SuppressLint("Range")
    fun obtenerContactoPorMovil(movil: String): Contacto? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_CONTACTOS, arrayOf(KEY_ID, KEY_NICK, KEY_MOVIL, KEY_APELLIDO1, KEY_APELLIDO2, KEY_NOMBRE, KEY_EMAIL),
            "$KEY_MOVIL=?", arrayOf(movil), null, null, null, null)
        return if (cursor.moveToFirst()) {
            val contacto = Contacto(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NICK)),
                cursor.getString(cursor.getColumnIndex(KEY_MOVIL)),
                cursor.getString(cursor.getColumnIndex(KEY_APELLIDO1)),
                cursor.getString(cursor.getColumnIndex(KEY_APELLIDO2)),
                cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)),
                cursor.getString(cursor.getColumnIndex(KEY_EMAIL))
            )
            cursor.close()
            contacto
        } else {
            null
        }
    }

    // Eliminar un contacto a partir de su nick
    fun eliminarContactoPorNick(nick: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_CONTACTOS, "$KEY_NICK=?", arrayOf(nick))
    }

    // Editar los campos móvil y email
    fun editarMovilYEmail(contacto: Contacto, nuevoMovil: String, nuevoEmail: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_MOVIL, nuevoMovil)
        values.put(KEY_EMAIL, nuevoEmail)
        return db.update(TABLE_CONTACTOS, values, "$KEY_ID=?", arrayOf(contacto.id.toString()))
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "AgendaContactos"
        private const val TABLE_CONTACTOS = "contactos"
        private const val KEY_ID = "id"
        private const val KEY_NICK = "nick"
        private const val KEY_MOVIL = "movil"
        private const val KEY_APELLIDO1 = "apellido1"
        private const val KEY_APELLIDO2 = "apellido2"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_EMAIL = "email"
    }
}