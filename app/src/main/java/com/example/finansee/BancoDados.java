package com.example.finansee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDados extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "FinanSee.db";
    private static final int VERSAO_BANCO = 1;

    public BancoDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE movimentacoes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "descricao TEXT, " +
                        "valor REAL, " +
                        "tipo TEXT, " +
                        "categoria TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS movimentacoes");
        onCreate(db);
    }

    public void inserirMovimentacao(String descricao,
                                    double valor,
                                    String tipo,
                                    String categoria) {

        SQLiteDatabase db = this.getWritableDatabase();

        android.content.ContentValues dados =
                new android.content.ContentValues();

        dados.put("descricao", descricao);
        dados.put("valor", valor);
        dados.put("tipo", tipo);
        dados.put("categoria", categoria);

        db.insert("movimentacoes", null, dados);

        db.close();
    }

    public android.database.Cursor listarMovimentacoes() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM movimentacoes ORDER BY id DESC",
                null
        );
    }
    public double calcularSaldo() {

        SQLiteDatabase db = this.getReadableDatabase();

        android.database.Cursor cursor = db.rawQuery(
                "SELECT SUM(CASE WHEN tipo = 'Receita' " +
                        "THEN valor ELSE -valor END) FROM movimentacoes",
                null
        );

        double saldo = 0;

        if (cursor.moveToFirst()) {
            saldo = cursor.getDouble(0);
        }

        cursor.close();

        return saldo;
    }
}