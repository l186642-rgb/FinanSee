package com.example.finansee;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import android.widget.ArrayAdapter;

public class MovimentacoesActivity extends AppCompatActivity {
    private BancoDados bancoDados;
    private ArrayList<String> movimentacoes = new ArrayList<>();
    // Lista dinâmica que armazena textos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movimentacoes);
        bancoDados = new BancoDados(this);
        android.database.Cursor cursor =
                bancoDados.listarMovimentacoes();

        movimentacoes.clear();

        while (cursor.moveToNext()) {

            String descricao = cursor.getString(1);
            double valor = cursor.getDouble(2);
            String tipo = cursor.getString(3);
            String categoria = cursor.getString(4);

            String movimentacao = tipo + " - " + descricao
                    + " - R$ " + valor + " - " + categoria;

            movimentacoes.add(movimentacao);
        }

        cursor.close();
        EditText edtDescricao = findViewById(R.id.edtDescricao);
        EditText edtValor = findViewById(R.id.edtValor);

        RadioGroup rgTipo = findViewById(R.id.rgTipo);

        Spinner spCategoria = findViewById(R.id.spCategoria);
        String[] categorias = {
                "Alimentação",
                "Transporte",
                "Moradia",
                "Saúde",
                "Educação",
                "Lazer",
                "Salário",
                "Outros"
        };
        // Conecta a lista de categorias ao Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategoria.setAdapter(adapter);
        Button btnSalvar = findViewById(R.id.btnSalvar);

        TextView txtListaVazia = findViewById(R.id.txtListaVazia);
        String historico = "";

        for (String item : movimentacoes) {
            historico += item + "\n\n";
        }

        if (historico.isEmpty()) {
            txtListaVazia.setText("Nenhuma movimentação cadastrada.");
        } else {
            txtListaVazia.setText(historico);
        }
        TextView txtSaldo = findViewById(R.id.txtSaldo);
        double saldo = bancoDados.calcularSaldo();

        txtSaldo.setText(String.format(
                java.util.Locale.getDefault(),
                "Saldo atual: R$ %.2f",
                saldo
        ));
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String descricao = edtDescricao.getText().toString();
                String valorTexto = edtValor.getText().toString();

                int tipoSelecionado = rgTipo.getCheckedRadioButtonId();

                if (descricao.isEmpty() || valorTexto.isEmpty()
                        || tipoSelecionado == -1) {

                    Toast.makeText(MovimentacoesActivity.this,
                            "Preencha todos os campos!",
                            Toast.LENGTH_SHORT).show();

                } else {

                    try {
                        double valor = Double.parseDouble(
                                valorTexto.replace(",", "."));

                        if (valor <= 0) {
                            Toast.makeText(MovimentacoesActivity.this,
                                    "Digite um valor maior que zero!",
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            String tipo;

                            if (tipoSelecionado == R.id.rbReceita) {
                                tipo = "Receita";
                            } else {
                                tipo = "Despesa";
                            }

                            String categoria = String.valueOf(
                                    spCategoria.getSelectedItem());

                            String movimentacao = tipo + " - " + descricao
                                    + " - R$ " + valor + " - " + categoria;

                            movimentacoes.add(movimentacao);
                            bancoDados.inserirMovimentacao(
                                    descricao,
                                    valor,
                                    tipo,
                                    categoria
                            );
                            double novoSaldo = bancoDados.calcularSaldo();

                            txtSaldo.setText(String.format(
                                    java.util.Locale.getDefault(),
                                    "Saldo atual: R$ %.2f",
                                    novoSaldo
                            ));
                            String historico = "";

                            for (String item : movimentacoes) {
                                historico += item + "\n\n";
                            }

                            txtListaVazia.setText(historico);

                            Toast.makeText(MovimentacoesActivity.this,
                                    "Movimentação cadastrada!",
                                    Toast.LENGTH_SHORT).show();

                            edtDescricao.setText("");
                            edtValor.setText("");
                            rgTipo.clearCheck();
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(MovimentacoesActivity.this,
                                "Digite um valor válido!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}