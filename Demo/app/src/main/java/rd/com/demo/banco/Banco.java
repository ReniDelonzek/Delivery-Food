package rd.com.demo.banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;


public class Banco extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "Carinho.db";
    public static final String NOME_TABELA = "Carinho";

    public static final String PRODUTO = "Produto";
    public static final String CODIGO = "Codigo";
    public static final String ESTABELECIMENTO_CODE = "Estabelecimento_code";
    public static final String PRECO = "Preco";
    public static final String ESTABELECIMENTO = "Estabelecimento";
    public static final String QUANTIDADE = "Quantidade";
    public static final String URL_IMAGEM = "Url";


    public Banco(Context context) {
        super(context, NOME_BANCO, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NOME_TABELA + " (" + _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PRODUTO + " TEXT NOT NULL, " + CODIGO + " INTEGER, " +
                ESTABELECIMENTO_CODE + " INTEGER, " + PRECO + " INTEGER, " +
                ESTABELECIMENTO + " TEXT, " + QUANTIDADE + " INTEGER, " +
                URL_IMAGEM + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + NOME_TABELA);
        onCreate(db);
    }
}
