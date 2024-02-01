package com.github.Ringoame196.Data

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class DatabaseFile {
    fun make(databaseUrl: String) {
        var connection: Connection? = null
        var statement: Statement? = null

        try {
            // JDBC ドライバをロード
            Class.forName("org.sqlite.JDBC")

            // データベースに接続
            connection = DriverManager.getConnection(databaseUrl)

            // ステートメントを作成
            statement = connection.createStatement()

            // テーブルを作成する SQL クエリ
            val createTableQuery = """
            CREATE TABLE IF NOT EXISTS player_data (
                uuid TEXT PRIMARY KEY,
                balance INTEGER
            )
            """.trimIndent()

            // テーブルを作成
            statement.executeUpdate(createTableQuery)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // リソースを解放
            statement?.close()
            connection?.close()
        }
    }
}
