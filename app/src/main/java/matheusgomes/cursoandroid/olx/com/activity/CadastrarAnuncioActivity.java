package matheusgomes.cursoandroid.olx.com.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import matheusgomes.cursoandroid.olx.com.R;
import matheusgomes.cursoandroid.olx.com.databinding.ActivityCadastrarAnuncioBinding;
import matheusgomes.cursoandroid.olx.com.helper.Permissoes;

public class CadastrarAnuncioActivity extends AppCompatActivity
implements View.OnClickListener {

    private ActivityCadastrarAnuncioBinding binding;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastrarAnuncioBinding.inflate( getLayoutInflater() );

        setContentView( binding.getRoot() );

        //Validar permissões
        Permissoes.validarPermissoes( permissoes, this, 1 );

        binding.imageCadastro1.setOnClickListener( this );
        binding.imageCadastro2.setOnClickListener( this );
        binding.imageCadastro3.setOnClickListener( this );

        carregarDadosSpinner();

    }

    @Override
    public void onClick(View view) {

        switch ( view.getId() ){

            case R.id.imageCadastro1 :
                escolherImagem( 1 );
                break;

            case R.id.imageCadastro2 :
                escolherImagem( 2 );
                break;

            case R.id.imageCadastro3 :
                escolherImagem( 3 );
                break;


        }

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

    public void validarDadosAnuncio( View view ){



    }

    public void salvarAnuncio(){

        String valor = binding.editTelefone.getText().toString();
        Log.d( "salvar", "salvarAnuncio: " + valor );

    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == Activity.RESULT_OK){

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if( requestCode == 1 ){
                binding.imageCadastro1.setImageURI( imagemSelecionada );
            }else if( requestCode == 2 ){
                binding.imageCadastro2.setImageURI( imagemSelecionada );
            }else if( requestCode == 3 ){
                binding.imageCadastro3.setImageURI( imagemSelecionada );
            }

            listaFotosRecuperadas.add( caminhoImagem );

        }

    }

    private void carregarDadosSpinner(){

        String[] estados = getResources().getStringArray( R.array.estados );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource( com.google.android.material.R.layout.support_simple_spinner_dropdown_item );
        binding.spinnerEstado.setAdapter( adapter );

        String[] categorias = getResources().getStringArray( R.array.categoria );
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                categorias
        );
        adapterCategoria.setDropDownViewResource( com.google.android.material.R.layout.support_simple_spinner_dropdown_item );
        binding.spinnerCategoria.setAdapter( adapterCategoria );

    }
}