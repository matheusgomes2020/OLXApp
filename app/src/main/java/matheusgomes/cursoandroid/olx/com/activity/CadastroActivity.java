package matheusgomes.cursoandroid.olx.com.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import matheusgomes.cursoandroid.olx.com.databinding.ActivityCadastroBinding;
import matheusgomes.cursoandroid.olx.com.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastroBinding.inflate( getLayoutInflater() );

        setContentView( binding.getRoot() );

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        binding.buttonAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.editCadastroEmail.getText().toString();
                String senha = binding.editCadastroSenha.getText().toString();

                if( !email.isEmpty() ){
                    if( !senha.isEmpty() ){

                        //Verifica estado do switch
                        if( binding.switchAcessp.isChecked() ){//Cadastro

                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if( task.isSuccessful() ){

                                        Toast.makeText(CadastroActivity.this,
                                                "Cadastro realizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();

                                        //Direcionar para a tela principal do App

                                    }else {

                                        String erroExcecao = "";

                                        try{
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            erroExcecao = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            erroExcecao = "Por favor, digite um e-mail v??lido";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            erroExcecao = "Este conta j?? foi cadastrada";
                                        } catch (Exception e) {
                                            erroExcecao = "ao cadastrar usu??rio: "  + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(CadastroActivity.this,
                                                "Erro: " + erroExcecao ,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else {//Login

                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if( task.isSuccessful() ){

                                        Toast.makeText(CadastroActivity.this,
                                                "Logado com sucesso",
                                                Toast.LENGTH_SHORT).show();

                                        startActivity( new Intent( getApplicationContext(), AnunciosActivity.class ));

                                    }else {
                                        Toast.makeText(CadastroActivity.this,
                                                "Erro ao fazer login : " + task.getException() ,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }

                    }else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}