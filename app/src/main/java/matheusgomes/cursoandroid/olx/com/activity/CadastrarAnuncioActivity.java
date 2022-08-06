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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import matheusgomes.cursoandroid.olx.com.R;
import matheusgomes.cursoandroid.olx.com.databinding.ActivityCadastrarAnuncioBinding;
import matheusgomes.cursoandroid.olx.com.helper.ConfiguracaoFirebase;
import matheusgomes.cursoandroid.olx.com.helper.Permissoes;
import matheusgomes.cursoandroid.olx.com.model.Anuncio;

public class CadastrarAnuncioActivity extends AppCompatActivity
implements View.OnClickListener {

    private ActivityCadastrarAnuncioBinding binding;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();

    private Anuncio anuncio;

    private StorageReference storage;

    private android.app.AlertDialog dialog;

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

        storage = ConfiguracaoFirebase.getFirebaseStorage();

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

    private Anuncio configurarAnuncio(){

        String estado = binding.spinnerEstado.getSelectedItem().toString();
        String categoria = binding.spinnerCategoria.getSelectedItem().toString();
        String titulo = binding.editTitulo.getText().toString();
        String valor = binding.editValor.getText().toString();
        String telefone = binding.editTelefone.getUnMasked().toString();
        String descricao = binding.editDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado( estado );
        anuncio.setCategoria( categoria );
        anuncio.setTitulo( titulo );
        anuncio.setValor( valor );
        anuncio.setTelefone( telefone );
        anuncio.setDescricao( descricao );

        return anuncio;

    }

    public void validarDadosAnuncio( View view ){

        anuncio = configurarAnuncio();

        String valor = String.valueOf( binding.editValor.getRawValue() );

        if ( listaFotosRecuperadas.size() != 0 ){
            if ( !anuncio.getEstado().isEmpty() ){
                if ( !anuncio.getCategoria().isEmpty() ){
                    if ( !anuncio.getTitulo().isEmpty() ){
                        if ( !valor.isEmpty() && !valor.equals("0") ){
                            if ( !anuncio.getTelefone().isEmpty() && anuncio.getTelefone().length() >=11 ){
                                if ( !anuncio.getDescricao().isEmpty() ){
                                    salvarAnuncio();
                                }else {
                                    exibirMensagemErro( "Preencha o campo descricao!" );
                                }
                            }else {
                                exibirMensagemErro( "Preencha o campo telefone, digite ao menos 10 números!" );
                            }
                        }else {
                            exibirMensagemErro( "Preencha o campo valor!" );
                        }
                    }else {
                        exibirMensagemErro( "Preencha o campo titulo!" );
                    }
                }else {
                    exibirMensagemErro( "Preencha o campo categoria!" );
                }
            }else {
                exibirMensagemErro( "Preencha o campo estado!" );
            }

        }else {
            exibirMensagemErro( "Selecione ao menos uma foto!" );
        }


    }

    private void exibirMensagemErro( String mensagem ){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void salvarAnuncio(){

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage( "Salvando anúncio" )
                .setCancelable( false )
                .build();

        dialog.show();
        /**
         * Salvarimagem no Storage
         */
        for ( int i=0; i < listaFotosRecuperadas.size(); i++ ){

            String urlImagem = listaFotosRecuperadas.get( i );
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage( urlImagem, tamanhoLista, i );

        }

    }

    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {

        //Criar nó no Storage
        StorageReference imagemAnuncio = storage.child( "imagens" )
                .child( "anuncios" )
                .child( anuncio.getIdAnuncio() )
                .child( "imagem" + contador );

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile( Uri.parse( urlString ) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri firebaseurl = task.getResult();
                        String urlConvertida = firebaseurl.toString();

                        listaUrlFotos.add( urlConvertida );

                        if ( totalFotos == listaUrlFotos.size() ){

                            anuncio.setFotos( listaUrlFotos );
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();


                        }

                    }
                });



                //progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro( "Falha ao fazer upload" );
                Log.d( "INFO", "Falha ao fazer upload: " + e.getMessage() );
            }
        });

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

        //Configura spinner de estados
        String[] estados = getResources().getStringArray( R.array.estados );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource( com.google.android.material.R.layout.support_simple_spinner_dropdown_item );
        binding.spinnerEstado.setAdapter( adapter );

        //Configura spinner de categorias
        String[] categorias = getResources().getStringArray( R.array.categoria );
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                categorias
        );
        adapterCategoria.setDropDownViewResource( com.google.android.material.R.layout.support_simple_spinner_dropdown_item );
        binding.spinnerCategoria.setAdapter( adapterCategoria );

    }
}