package matheusgomes.cursoandroid.olx.com.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import matheusgomes.cursoandroid.olx.com.R;
import matheusgomes.cursoandroid.olx.com.adapter.AdapterAnuncios;
import matheusgomes.cursoandroid.olx.com.databinding.ActivityAnunciosBinding;
import matheusgomes.cursoandroid.olx.com.helper.ConfiguracaoFirebase;
import matheusgomes.cursoandroid.olx.com.model.Anuncio;

public class AnunciosActivity extends AppCompatActivity {

    private ActivityAnunciosBinding binding;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private FirebaseAuth autenticacao;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());;

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child( "anuncios" );


        //Configurar RecyclerView
        binding.recyclerAnunciosPublicos.setLayoutManager( new LinearLayoutManager( this ));
        binding.recyclerAnunciosPublicos.setHasFixedSize( true );
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this );
        binding.recyclerAnunciosPublicos.setAdapter( adapterAnuncios );

        recuperarAnunciosPublicos();

    }

    public void filtrarPorEstado( View view ){

        AlertDialog.Builder dialogEstado = new AlertDialog.Builder( this );
        dialogEstado.setTitle( "Selecione o estado desejado" );

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate( R.layout.dialog_spinner, null );

        //Configura spinner de estados
        Spinner spinnerEstado = viewSpinner.findViewById( R.id.spinnerFiltro );
        String[] estados = getResources().getStringArray( R.array.estados );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource( com.google.android.material.R.layout.support_simple_spinner_dropdown_item );
        spinnerEstado.setAdapter( adapter );

        dialogEstado.setView( viewSpinner );

        dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filtroEstado = spinnerEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                filtrandoPorEstado = true;
            }
        });

        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = dialogEstado.create();
        dialog.show();

    }

    public void filtrarPorCategoria( View view ){

        if ( filtrandoPorEstado == true ){

            AlertDialog.Builder dialogCategoria = new AlertDialog.Builder( this );
            dialogCategoria.setTitle( "Selecione a categoria desejada" );

            //Configurar spinner
            View viewSpinner = getLayoutInflater().inflate( R.layout.dialog_spinner, null );

            //Configura spinner de estados
            Spinner spinnerCategoria = viewSpinner.findViewById( R.id.spinnerFiltro );
            String[] categorias = getResources().getStringArray( R.array.categoria );
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item,
                    categorias
            );
            adapter.setDropDownViewResource( com.google.android.material.R.layout.support_simple_spinner_dropdown_item );
            spinnerCategoria.setAdapter( adapter );

            dialogCategoria.setView( viewSpinner );

            dialogCategoria.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorEstado();
                    recuperarAnunciosPorCategoria();
                }
            });

            dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = dialogCategoria.create();
            dialog.show();

        }else {
            Toast.makeText(this, "Escolha primeiro uma região!", Toast.LENGTH_SHORT).show();
        }

    }

    private void recuperarAnunciosPorEstado() {

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage( "Recuperando anúncios" )
                .setCancelable( false )
                .build();

        dialog.show();

        //Configura nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child( "anuncios" )
                .child( filtroEstado );

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAnuncios.clear();
                for ( DataSnapshot categorias : snapshot.getChildren() ){
                    for ( DataSnapshot anuncios : categorias.getChildren() ){
                        Anuncio anuncio = anuncios.getValue( Anuncio.class );
                        listaAnuncios.add( anuncio );
                    }
                }

                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarAnunciosPorCategoria() {



        //Configura nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child( "anuncios" )
                .child( filtroEstado )
                .child( filtroCategoria );

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAnuncios.clear();
                for ( DataSnapshot anuncios : snapshot.getChildren() ){

                        Anuncio anuncio = anuncios.getValue( Anuncio.class );
                        listaAnuncios.add( anuncio );

                }

                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if ( autenticacao.getCurrentUser() == null ){//Usuario deslogado
            menu.setGroupVisible( R.id.group_deslogado, true );
        }else {//Usuario logado
            menu.setGroupVisible( R.id.group_logado, true );
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ){

            case R.id.menu_cadastrar:
                startActivity( new Intent( getApplicationContext(), CadastroActivity.class ));
                break;

            case R.id.menu_sair :
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_anuncios :
                startActivity( new Intent( getApplicationContext(), MeusAnunciosActivity.class ) );

        }

        return super.onOptionsItemSelected(item);
    }

    private void recuperarAnunciosPublicos() {

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage( "Recuperando anúncios" )
                .setCancelable( false )
                .build();

        dialog.show();

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaAnuncios.clear();
                for ( DataSnapshot estados : snapshot.getChildren(  ) ){
                    for ( DataSnapshot categorias : estados.getChildren() ){
                        for ( DataSnapshot anuncios : categorias.getChildren() ){
                            Anuncio anuncio = anuncios.getValue( Anuncio.class );
                            listaAnuncios.add( anuncio );
                        }
                    }
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}