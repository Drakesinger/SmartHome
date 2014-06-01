package ch.hearc.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText editText;
	private EditText TextMdp;
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void Enregistrement(View view){
		//Intent intentEnregistrement = new Intent(this, LoginActivity.class);
	}
	public void sendMessage(View view){
		Intent intent = new Intent(this, HomeActivity.class);
		editText = (EditText) findViewById(R.id.editText1);
		TextMdp = (EditText) findViewById(R.id.editText2);
		String mdp = TextMdp.getText().toString();
		String message = editText.getText().toString();
		
		if(mdp.equals("pass") && message.equals("user"))
		{
			Toast.makeText(LoginActivity.this, "Login OK !", Toast.LENGTH_LONG).show();
			intent.putExtra(EXTRA_MESSAGE, message);
			startActivity(intent);
				
		}
		else
		{
			Toast.makeText(LoginActivity.this, "Mauvaise combinaison login/mdp !", Toast.LENGTH_LONG).show();
		}
	}
}
