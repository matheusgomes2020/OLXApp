package matheusgomes.cursoandroid.olx.com.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

        recuperarAnuncios();

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

    private void recuperarAnuncios() {

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