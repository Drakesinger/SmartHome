package ch.hearc.smarthome.networktester;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SHBluetoothNetworkManager extends Application{

	/* SDP (Service Discovery Protocol) Name */
	private static final String 	NAME 	= "SmartHome Bluetooth";
	
	/* Debugging */
	public static final boolean 	DEBUG 	= true;
	private static final boolean 	INFO	= true;
	private static final String 	TAG 	= "SHBluetoothNetworkManager";
	
	/* Member fields */
	private final 	BluetoothAdapter	mBtAdapter;
	private 		Handler 			mHandler;
	
	private 		AcceptThread 		mAcceptThread;
	private 		ConnectThread 		mConnectThread;
	private 		ConnectedThread 	mConnectedThread;
	
	private int 	mState;
	private boolean bBusy;
	private boolean bStoppingConnection;

	/* UUIDs for this application */
	/* This is the base UUID in order to establish an RFCOMM channel with the PIC module */
	private static final UUID BASE_UUID		= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	/* Constants to indicate message contents */
	public static final int MSG_OK 			= 0;
	public static final int MSG_READ 		= 1;
	public static final int MSG_WRITE 		= 2;
	public static final int MSG_CANCEL 		= 3;
	public static final int MSG_CONNECTED 	= 4;

	/* Bluetooth States */
	public static final int STATE_NONE 			= 0; // we're doing nothing
	public static final int STATE_LISTEN 		= 1; // now listening for incoming connections
	public static final int STATE_CONNECTING 	= 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED 	= 3; // now connected to a remote device

	
	/** Constructor for the network manager */
	public SHBluetoothNetworkManager(BluetoothAdapter _bluetoothAdapter){
		mBtAdapter = _bluetoothAdapter;
		mState = STATE_NONE;
		mHandler = null;
	}
	
	/** Constructor for the network manager */
	public SHBluetoothNetworkManager(){
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = null;
	}

	/**
	 * Return the current connection state.
	 * 
	 * @return {@link int} mState
	 * integer representing the State of the Bluetooth connection.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Change the actual state of the Bluetooth connection.
	 * 
	 * @param _mState
	 *            the State to set
	 */
	public synchronized void setState(int _mState) {
		mState = _mState;

		/* If we set the State to something, update it by using the Handler */
		//mHandler.obtainMessage(SHBluetoothTesting.MESSAGE_STATE_CHANGE,_mState, -1).sendToTarget();
	}
	

	/**
	 * Start the Bluetooth service. Start a new AcceptThread by entering a
	 * listening state. Start is ran by the {@link Activity}'s {@link OnResume}
	 * method.
	 */
	public synchronized void start() {
		if (DEBUG) {
			Log.d(TAG, "Starting bluetooth manager.");
		}
		
		/* Stop any previous client connection thread */
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		/* Stop any previous connected thread manager */
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		/* Start our listener thread */
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}

		setState(STATE_LISTEN);

	}

	public synchronized void stop() {
		if (DEBUG) {
			Log.d(TAG, "Stopping bluetooth manager.");
		}
		
		/* Stop our listener thread */
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		/* Stop our client connection thread */
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		/* Stop our connected thread manager */
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		/* We aren't doing anything anymore so set it to correct state. */
		setState(STATE_NONE);
	}

	/** TODO documentation */
	public synchronized void connect(BluetoothDevice _device) {
		if (DEBUG) {
			Log.d(TAG, "Connect to " + _device);
		}
		
		/* Cancel any connecting threads */
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		/* Cancel any running threads that are connected */

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		/* Start the connect thread in order to connect to the device */
		mConnectThread = new ConnectThread(_device);
		mConnectThread.start();
		setState(STATE_CONNECTED);

	}
	
	/** TODO documentation */
	public synchronized void connected(BluetoothSocket _socket, BluetoothDevice _device) {
		if (DEBUG) {
			Log.d(TAG, "Connected on " + _socket + " with " + _device);
		}
		
		/* Cancel the thread that completed the connection */
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		/* Cancel any thread currently running a connection */
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		/*
		 * Cancel the accept thread because we only want to connect to one
		 * device
		 */
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		/*
		 * Start the connected thread to manage the connection and perform the
		 * transmissions
		 */
		mConnectedThread = new ConnectedThread(_socket);
		mConnectedThread.start();

		/*
		 * Now send the name of the connected device using the Handler, back to
		 * the UI
		 */
		/*
		Message messageName = mHandler.obtainMessage(SHBluetoothTesting.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(SHBluetoothTesting.DEVICE_NAME, _device.getName());
		messageName.setData(bundle);
		mHandler.sendMessage(messageName);
		*/


		setState(STATE_CONNECTED);
		sendMessage(MSG_CONNECTED, _device);

	}
	
	/** Disconnects and stops all threads */
	public synchronized void disconnect(/*BluetoothDevice _device*/) {
		/* Only disconnect once */
		if (!bStoppingConnection) {
			bStoppingConnection = true;
			
			if (DEBUG) {
				Log.d(TAG, "Disconnecting.");
			}
			/* Cancel any running connected threads */
			if (mConnectedThread != null) {
				mConnectedThread.cancel();
				mConnectedThread = null;
			}
			/* Cancel any connecting threads */
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
			/* Cancel any listener threads */
			if (mAcceptThread != null) {
				mAcceptThread.cancel();
				mAcceptThread = null;
			}
			setState(STATE_NONE);
			sendMessage(MSG_CANCEL, "Connection ended");
		}

	}
	
	
	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled).
	 */
	private class AcceptThread extends Thread {
		/* Local server socket */
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			if (INFO) {
				Log.i(TAG, "Constructing AcceptThread");
			}
			
			/*
			 * Use a temporary object that is later assigned to mmServerSocket,
			 * because mmServerSocket is final
			 */
			BluetoothServerSocket tmp = null;
			/* Create a new listening server socket */
			try {
				/*
				 * MY_UUID is the app's UUID string, also used by the client
				 * code
				 */
				tmp = mBtAdapter.listenUsingRfcommWithServiceRecord(NAME, BASE_UUID);
				if (INFO) {
					Log.i(TAG, "AcceptThread: bluetooth server socket = " + tmp);
				}
			} catch (IOException e) {
				Log.e(TAG, "Listen Socket creation failed. listen()", e);
				
				// TODO use the handler to send the error message to the UI
				exceptionManager("Socket creation failed, listen() failure.", false);

			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (DEBUG) {
				Log.d(TAG, "BEGIN mAcceptThread" + this);
			}
			
			setName("AcceptThread");

			BluetoothSocket socket = null;

			/*
			 * Keep listening to the server socket if we are not not connected
			 * or until exception occurs or a socket is returned
			 */
			while (mState != STATE_CONNECTED) {

				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					// TODO use the handler to send the error message to the UI
					exceptionManager("accept() failed.", false);
					break;
				}

				/*
				 * If a connection was accepted, check in which state we are.
				 * 
				 * STATE_LISTEN : do nothing 
				 * STATE_CONNECTING : trying to connect, start the connection manager thread 
				 * STATE_NONE : do nothing 
				 * STATE_CONNECTED : already connected or error, terminate the new socket
				 */
				if (socket == null) {
					Log.d(TAG, "I am here!");
				}
				if (socket != null) {
					Log.d(TAG, "socket != null | state: " + mState);
					synchronized (SHBluetoothNetworkManager.this) {
						switch (mState) {
						case STATE_LISTEN:
							if (INFO) {
								Log.i(TAG, "Accept thread - STATE_LISTEN");
							}
						case STATE_CONNECTING:
							if (INFO) {
								Log.i(TAG, "Accept thread - STATE_CONNECTING");
							}
							/* Start the connected thread */
							connected(socket, socket.getRemoteDevice());
							break;
						case STATE_NONE:
							if (INFO) {
								Log.i(TAG, "Accept thread - STATE_NONE");
							}
						case STATE_CONNECTED:
							if (INFO) {
								Log.i(TAG, "Accept thread - STATE_CONNECTED");
							}
							
							/*
							 * We may already be connected. So we terminate the
							 * newly created socket
							 */
							try {
								/*
								 * TODO Do work to manage the connection (in a
								 * separate thread)
								 */
								// manageConnectedSocket(socket);
								socket.close();
							} catch (Exception e) {
								Log.e(TAG, "Could not close socket", e);
								exceptionManager("Already connected. Could not close socket.", false);
							}
							break;
						}
					}

				}
			}
			
			if (INFO) {
				Log.i(TAG, "END mAcceptThread" + this);
			}
		}

		/* Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server socket failed", e);
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			/*
			 * Use a temporary object that is later assigned to mmSocket,
			 * because mmSocket is final
			 */
			BluetoothSocket tmp = null;
			mmDevice = device;

			/* Get a BluetoothSocket to connect with the given BluetoothDevice */
			try {
				/* MY_UUID is the app's UUID string, also used by the server code */
				tmp = device.createRfcommSocketToServiceRecord(BASE_UUID);
			} catch (IOException e) {
				Log.e(TAG, "create() failed", e);
				exceptionManager("Socket creation failure. create() failed.", false);
			}
			mmSocket = tmp;
		}

		public void run() {
			if (INFO) {
				Log.i(TAG, "BEGIN mConnectThread");
			}
			setName("ConnectThread");
			
			try {
				/*
				 * Connect the device through the socket. This will block until
				 * it succeeds or throws an exception
				 */
				mmSocket.connect();
			} catch (IOException connectException) {
				/* Unable to connect; close the socket and get out */
				Log.e(TAG, "unable to connect", connectException);
				try {
					mmSocket.close();
				} catch (IOException closeException) {
					Log.e(TAG,"unable to close() socket during connection failure",closeException);
				}
				if (DEBUG) Log.d(TAG, "Unable to connect device.");
				exceptionManager("Unable to connect device.", true);
				return;
			}

			/* Reset the thread when done */
			synchronized (SHBluetoothNetworkManager.this) {
				if (DEBUG) Log.d(TAG, "Resetting mConnectThread");
				mConnectThread = null;
			}

			/* Start the connected thread manager */
			connected(mmSocket, mmDevice);

			// TODO Do work to manage the connection (in a separate thread)
			// manageConnectedSocket(mmSocket);
			
			if (INFO) Log.i(TAG, "END mConnectThread");
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
				exceptionManager("close() of connect socket failed", false);
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "Connected Thread constuctor called");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			/*
			 * Get the input and output streams, using temp objects because
			 * member streams are final
			 */
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "ConnectedThread: Failed to get input and output streams.");
				disconnect();
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			if (INFO) {
				Log.i(TAG, "BEGIN mConnectedThread");
			}
			
			//setName("ConnectedThread");
			
			byte[] buffer = new byte[1024]; // buffer store for the stream
			int bytes; 						// bytes returned from read()
			String Input;
				
			/* Keep listening to the InputStream until an exception occurs */
			while (true) {
				try {
					if(DEBUG) Log.d(TAG, "Reading from InputStream");
					/* Read from the InputStream */
					bytes = mmInStream.read(buffer);
					
					if (bytes >0) {
						if(DEBUG) Log.d(TAG, "Trying to show input");
						Input = new String(buffer, "UTF-8").substring(0, bytes - 1);
						if(DEBUG) Log.v(TAG, "Read: " + Input);
						sendMessage(MSG_READ, Input);
					}
					
					/* Send the obtained bytes to the UI activity */
					//mHandler.obtainMessage(SHBluetoothTesting.MESSAGE_READ,bytes, -1, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "Disconnected from device.", e);
					exceptionManager("Device connection was lost. Restarting.", true);
					break;
				}
				
				bBusy = false;
			}
			
			if (INFO) {
				Log.i(TAG, "END mConnectedThread");
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public boolean write(/*byte[]*/ String bytes) {
			try {
				if (DEBUG) {
					Log.d(TAG, "ConnectedThread. Writing out.");
				}
				if (bytes != null) {
					sendMessage(MSG_WRITE, bytes.toString());
					mmOutStream.write(bytes.getBytes());
					
					/*
					 * Use the Handler's obtainMessage method to share the sent data
					 * to the UI
					 */
					//mHandler.obtainMessage(SHBluetoothTesting.MESSAGE_WRITE, -1, -1, bytes).sendToTarget();
				}
				mmOutStream.write('\r');
				return true;
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
			return false;
		}
		
		public String read(){
			
			byte[] buffer = new byte[1024]; // buffer store for the stream
			int bytes; 						// bytes returned from read()
			String Input;
				
			/* Keep listening to the InputStream until an exception occurs */
			while (true) {
				try {
					if(DEBUG) Log.d(TAG, "Reading from InputStream");
					/* Read from the InputStream */
					bytes = mmInStream.read(buffer);
					
					if (bytes >0) {
						if(DEBUG) Log.d(TAG, "Trying to show input");
						Input = new String(buffer, "UTF-8").substring(0, bytes - 1);
						if(DEBUG) Log.v(TAG, "Read: " + Input);
						sendMessage(MSG_READ, Input);
					}
					
					/* Send the obtained bytes to the UI activity */
					//mHandler.obtainMessage(SHBluetoothTesting.MESSAGE_READ,bytes, -1, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "Disconnected from device.", e);
					exceptionManager("Device connection was lost. Restarting.", true);
					break;
				}
				
				bBusy = false;
			}
			
			return "no message";
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			if (DEBUG) {
				Log.d(TAG, "ConnectedThread cancel() called.");
			}
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
	/** TODO documentation */
	public boolean write(/*byte[]*/ String _out) {
		ConnectedThread conThread;

		synchronized (this) {
			if (mState != STATE_CONNECTED) {
				/* We are not connected so we can't write anything. */
				exceptionManager("Cannot write.\n"+"Not connected to any device.", false);
				// TODO PopupMessages.launchPopup("Write out","Not connected to any device.", null);
				
				if (DEBUG) {
					Log.d(TAG, "write() failed. not connected to anything");
				}
				return false;
			}
			conThread = mConnectedThread;
		}
		return conThread.write(_out);
	}



	/** TODO documentation */
	private void exceptionManager(String _errorDescription,boolean _restart) {
		Message message = mHandler.obtainMessage(SHBluetoothTesting.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(SHBluetoothTesting.TOAST, _errorDescription);
		message.setData(bundle);
		mHandler.sendMessage(message);

		if (_restart) {
			/* Failed to connect, restart the service in order to try again */
			SHBluetoothNetworkManager.this.start();
		}

	}

	/**
	 * Sends a message to the current activity registered to the activityHandler
	 * variable.
	 * 
	 * @param type
	 *            Type of message, use the public MSG_* constants
	 * @param value
	 *            Optional object to attach to message
	 */
	private synchronized void sendMessage(int type, Object value)
	{
		// It might happen that there's no activity handler, but here it doesn't prevent application work flow
		if(mHandler != null)
		{
			mHandler.obtainMessage(type, value).sendToTarget();
		}
	}

	/**
	 * Sets the current active activity handler so messages could be sent.
	 * 
	 * @param _handler
	 *            The current activity handler
	 */
	public void setActivityHandler(Handler _handler) {
		mHandler = _handler;
	}

}
