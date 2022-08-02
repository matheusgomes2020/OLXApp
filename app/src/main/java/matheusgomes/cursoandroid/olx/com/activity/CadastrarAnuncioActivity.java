package matheusgomes.cursoandroid.olx.com.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import matheusgomes.cursoandroid.olx.com.R;
import matheusgomes.cursoandroid.olx.com.databinding.ActivityCadastrarAnuncioBinding;
import matheusgomes.cursoandroid.olx.com.helper.Permissoes;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    private ActivityCadastrarAnuncioBinding binding;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastrarAnuncioBinding.inflate( getLayoutInflater() );

        setContentView( binding.getRoot() );

        //Validar permissões
        Permissoes.validarPermissoes( permissoes, this, 1 );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permissaoresultado : grantResults ){

            if ( permissaoresultado == PackageManager.PERMISSION_DENIED ){

                alertaValidacaoPermissao();

            }

        }

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "Permissões Negadas" );
        builder.setMessage( "Para utilizar o app é necessário aceitar as permissões" );
        builder.setCancelable( false );
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void salvarAnuncio(View view ){

        String valor = binding.editTelefone.getText().toString();
        Log.d( "salvar", "salvarAnuncio: " + valor );

    }

}