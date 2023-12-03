package com.github.Ringoame196

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

class Database {
    object dataBaseinfo {
        var connection: String = ""
        var userName: String = ""
        var password: String = ""
    }

    private fun connection(): Connection? {
        val connection = dataBaseinfo.connection
        val userName = dataBaseinfo.userName
        val password = dataBaseinfo.password
        return DriverManager.getConnection(
            connection,
            userName,
            password
        )
    }
    fun isExists(tableName: String, search: String, data: String): Boolean {
        val sql = "SELECT COUNT(*) FROM $tableName WHERE $search = ?"
        val preparedStatement = connection()?.prepareStatement(sql)

        return try {
            preparedStatement?.setString(1, data)
            val resultSet = preparedStatement?.executeQuery()
            resultSet?.next() ?: false && resultSet?.getInt(1)!! > 0
        } finally {
            preparedStatement?.close()
        }
    }
    fun insertStringString(tableName: String, search: String, setmass: String, data1: String, data2: String) {
        val sql = "INSERT INTO $tableName ($search, $setmass) VALUES (?, ?)"
        val preparedStatement = connection()?.prepareStatement(sql)

        try {
            preparedStatement?.setString(1, data1)
            preparedStatement?.setString(2, data2)
            preparedStatement?.executeUpdate()
            preparedStatement?.close()
        } finally {
            preparedStatement?.close()
        }
    }
    fun insertStringInt(tableName: String, search: String, setmass: String, data1: String, data2: Int) {
        val sql = "INSERT INTO $tableName ($search, $setmass) VALUES (?, ?)"
        val preparedStatement = connection()?.prepareStatement(sql)

        try {
            preparedStatement?.setString(1, data1)
            preparedStatement?.setInt(2, data2)
            preparedStatement?.executeUpdate()
            preparedStatement?.close()
        } finally {
            preparedStatement?.close()
        }
    }
    fun updateStrin(tableName: String, search: String, setmass: String, data1: String, data2: String) {
        val sql = "UPDATE $tableName SET $setmass = ? WHERE $search = ?"
        val preparedStatement = connection()?.prepareStatement(sql)

        try {
            preparedStatement?.setString(1, data1)
            preparedStatement?.setString(2, data2)
            preparedStatement?.executeUpdate()
            preparedStatement?.close()
        } finally {
            preparedStatement?.close()
        }
    }
    fun updateInt(tableName: String, search: String, setmass: String, data1: String, data2: Int) {
        val sql = "UPDATE $tableName SET $setmass = ? WHERE $search = ?"
        val preparedStatement = connection()?.prepareStatement(sql)

        try {
            preparedStatement?.setString(1, data1)
            preparedStatement?.setString(2, data2.toString()) // 文字列として設定する
            preparedStatement?.executeUpdate()
            preparedStatement?.close()
        } finally {
            preparedStatement?.close()
        }
    }
    fun getInt(tableName: String, search: String, getmass: String, playerUUID: String): Int {
        var getPoint = 0
        val sql = "SELECT * FROM $tableName WHERE $search = ?;"
        val selectStatement = connection()?.prepareStatement(sql)
        selectStatement?.setString(1, playerUUID)
        val resultSet = selectStatement?.executeQuery()
        if (resultSet?.next() == true) {
            getPoint = resultSet.getInt(getmass)
        }

        // 後処理
        postProcessing(selectStatement, connection())

        return getPoint
    }

    private fun postProcessing(selectStatement: PreparedStatement?, connection: Connection?) {
        selectStatement?.close()
        connection?.close()
    }
}
