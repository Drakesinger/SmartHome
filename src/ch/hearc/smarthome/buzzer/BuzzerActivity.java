package ch.hearc.smarthome.buzzer;

import android.os.Bundle;
import android.view.View;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHCommunicationProtocol;

public class BuzzerActivity extends SHBluetoothActivity {
	
	private static SHCommunicationProtocol protocol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buzzer_main);
		
		protocol = new SHCommunicationProtocol();
	}
	
	public void toInfiniteAndBeyond(View v){
		write(protocol.generateDataToSend("to infinite and beyond", " "));
	}
}
