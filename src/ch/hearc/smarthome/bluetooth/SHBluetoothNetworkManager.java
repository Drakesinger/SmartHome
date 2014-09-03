package ch.hearc.smarthome.bluetooth;

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
import android.os.Handler;
import android.util.Log;

/**
 * The main class of our application. Gets start up as soon as the application
 * starts.
 * 
 * Does all the work for setting up and managing a Bluetooth
 * connection with another devices.
 * 
 * <p>
 * The class has three threads:
 * 
 * <li> {@link AcceptThread}
 * <dl>
 * <dl>
 * A listener thread that opens a {@link BluetoothServerSocket} and listens for
 * incoming connection requests. When one is accepted, it provides a connected
 * {@link BluetoothSocket}. When the {@link BluetoothSocket} is acquired from
 * the {@link BluetoothServerSocket}, the {@link BluetoothServerSocket} is
 * discarded as we only want one connection. This thread allows a secure
 * bluetooth connection between two devices to be established.
 * </dl>
 * </dl></li>
 * 
 * <li> {@link ConnectThread}
 * <dl>
 * <dl>
 * A connecting thread from the client side, this thread requires the
 * information of a {@link BluetoothDevice}. This is obtained during
 * {@link SHDeviceSelectActivity}. Once the {@link BluetoothDevice} information
 * is received, we get a {@link BluetoothSocket} by calling
 * {@link #createRfcommSocketToServiceRecord} with the same {@link UUID} used in
 * the {@link AcceptThread} when calling
 * {@link #listenUsingRfcommWithServiceRecord}. The thread attempts to connect
 * on the {@link BluetoothSocket}, if able it resets and starts the
 * {@link ConnectedThread}.
 * </dl>
 * </dl></li>
 * 
 * <li> {@link ConnectedThread}
 * <dl>
 * <dl>
 * This thread handles all incoming and outgoing transmissions. It creates input
 * and output streams on the {@link BluetoothSocket} and then listens on the
 * {@link InputStream} of the {@link BluetoothSocket}. It also provides a
 * {@link #write(String)} method for sending data through the
 * {@link BluetoothSocket}. It has a thread for connecting with a device, a
 * thread for performing data transmissions when connected and a timeout thread
 * that cancels the connection when too much time has passed since last
 * communication.
 * </dl>
 * </dl></li>
 * 
 * <p>
 * If the connection is lost/broken/has failed/disconnected, the current
 * activity that is using it (that has set activityHander) should finish (a
 * message is dispatched to the current activityHander Handler). Then the
 * application will start with the Bluetooth device activity over again.
 * 
 * @author Horia Mut
 */
public class SHBluetoothNetworkManager extends Application
{
	// SDP (Service Discovery Protocol) Name 
	private static final String 	NAME 	= "SmartHome Bluetooth";

	// Debugging 
	public static final boolean 	DEBUG 		= true;
	public static final boolean 	INFO		= true;
	private static final String 	TAG 		= "SHBluetoothNetworkManager";
	
	// Member fields 
	private final 	BluetoothAdapter	mBtAdapter;
	private 		Handler 			mHandler;
	
	private 		AcceptThread 		mAcceptThread;
	private 		ConnectThread 		mConnectThread;
	private 		ConnectedThread 	mConnectedThread;
	
	private int 	mState;
	private boolean bBusy; // can be used to avoid sending over and over if blocked
	private boolean bStoppingConnection;

	// UUIDs for this application 
	// This is the base UUID in order to establish an RFCOMM channel with the PIC module 
	private static final UUID BASE_UUID		= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	//@formatter:off

	// Constants to indicate message contents 
	public static final int			MSG_OK				= 0;
	public static final int			MSG_READ			= 1;
	public static final int			MSG_WRITE			= 2;
	public static final int			MSG_CANCEL			= 3;
	public static final int			MSG_NOT_CONNECTED	= 4;
	public static final int			MSG_CONNECTED		= 5;
	public static final int			MSG_CONNECT_FAIL	= 6;
	public static final int			MSG_CONNECTION_LOST	= 7;

	
	// Bluetooth States 
	public static final int STATE_NONE 			= 0; // we're doing nothing
	public static final int STATE_LISTEN 		= 1; // now listening for incoming connections
	public static final int STATE_CONNECTING 	= 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED 	= 3; // now connected to a remote device
	
	//@formatter:on

	/** Constructor for the network manager */
	public SHBluetoothNetworkManager( )
	{
		mBtAdapter = BluetoothAdapter.getDefaultAdapter( );
		mState = STATE_NONE;
		mHandler = null;
	}

	/**
	 * Return the current connection state.
	 * 
	 * @return
	 *         integer representing the State of the Bluetooth connection.
	 */
	public synchronized int getState( )
	{
		return mState;
	}

	/**
	 * Change the actual state of the Bluetooth connection.
	 * 
	 * @param _mState
	 *            the State to set
	 */
	public synchronized void setState(int _mState)
	{
		mState = _mState;
	}

	/**
	 * Start the Bluetooth service. <br>
	 * Start a new AcceptThread by entering a
	 * listening state. <br>
	 * Start is run by the {@link Activity}'s {@link OnResume} method.
	 */
	public synchronized void start( )
	{
		if(DEBUG) Log.d(TAG, "Starting bluetooth manager.");

		// Stop any previous client connection thread
		if(mConnectThread != null)
		{
			mConnectThread.cancel( );
			mConnectThread = null;
		}

		// Stop any previous connected thread manager
		if(mConnectedThread != null)
		{
			mConnectedThread.cancel( );
			mConnectedThread = null;
		}

		// Start our listener thread
		if(mAcceptThread == null)
		{
			mAcceptThread = new AcceptThread( );
			mAcceptThread.start( );
		}

		setState(STATE_LISTEN);

	}

	/** Stop all threads, same as <code>disconnect</code>. */
	public synchronized void stop( )
	{
		if(DEBUG)

		Log.d(TAG, "Stopping bluetooth manager.");

		// Stop our listener thread
		if(mAcceptThread != null)
		{
			mAcceptThread.cancel( );
			mAcceptThread = null;
		}

		// Stop our client connection thread
		if(mConnectThread != null)
		{
			mConnectThread.cancel( );
			mConnectThread = null;
		}

		// Stop our connected thread manager
		if(mConnectedThread != null)
		{
			mConnectedThread.cancel( );
			mConnectedThread = null;
		}

		// We aren't doing anything anymore so set it to correct state.
		setState(STATE_NONE);
	}

	/**
	 * Connect to a Bluetooth device
	 * 
	 * @param _device
	 *            the Bluetooth device we want to connect to
	 */
	public synchronized void connect(BluetoothDevice _device)
	{
		if(DEBUG)

		Log.d(TAG, "Connect to " + _device);

		// Cancel any connecting threads
		if(mState == STATE_CONNECTING)
		{
			if(mConnectThread != null)
			{
				mConnectThread.cancel( );
				mConnectThread = null;
			}
		}

		// Cancel any running threads that are connected

		if(mConnectedThread != null)
		{
			mConnectedThread.cancel( );
			mConnectedThread = null;
		}

		// Start the connect thread in order to connect to the device
		mConnectThread = new ConnectThread(_device);
		mConnectThread.start( );
		setState(STATE_CONNECTED);

	}

	/**
	 * Start the connected thread in order to manage the connection
	 * 
	 * @param _socket
	 *            The Bluetooth socket given by the Bluetooth adapter
	 * @param _device
	 *            The Bluetooth device with which we are connected
	 */
	public synchronized void connected(BluetoothSocket _socket, BluetoothDevice _device)
	{
		if(DEBUG)

		Log.d(TAG, "Connected on " + _socket + " with " + _device);

		// Cancel the thread that completed the connection
		if(mConnectThread != null)
		{
			mConnectThread.cancel( );
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if(mConnectedThread != null)
		{
			mConnectedThread.cancel( );
			mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device

		if(mAcceptThread != null)
		{
			mAcceptThread.cancel( );
			mAcceptThread = null;
		}

		// Start the connected thread to manage the connection and perform the
		// transmissions

		mConnectedThread = new ConnectedThread(_socket);
		mConnectedThread.start( );

		// Now send the name of the connected device using the Handler, back to
		// the UI
		sendMessage(MSG_CONNECTED, _device.getName( ));

		// Set our state as connected
		setState(STATE_CONNECTED);

	}

	/** Disconnects and stops all threads */
	public synchronized void disconnect(/* BluetoothDevice _device */)
	{
		// Only disconnect once
		if(!bStoppingConnection)
		{
			bStoppingConnection = true;

			if(DEBUG) Log.d(TAG, "Disconnecting.");

			// Send the following data to the device so that it knows we
			// disconnected
			write(".,230,.");

			// Cancel any running connected threads
			if(mConnectedThread != null)
			{
				mConnectedThread.cancel( );
				mConnectedThread = null;
			}
			// Cancel any connecting threads
			if(mConnectThread != null)
			{
				mConnectThread.cancel( );
				mConnectThread = null;
			}
			// Cancel any listener threads
			if(mAcceptThread != null)
			{
				mAcceptThread.cancel( );
				mAcceptThread = null;
			}
			setState(STATE_NONE);
			sendMessage(MSG_CANCEL, "Connection ended");
		}

	}

	/**
	 * A listener thread that opens a {@link BluetoothServerSocket} and listens
	 * for incoming connection requests. When one is accepted, it provides a
	 * connected {@link BluetoothSocket}. When the {@link BluetoothSocket} is
	 * acquired from the {@link BluetoothServerSocket}, the
	 * {@link BluetoothServerSocket} is discarded as we only want one
	 * connection.
	 * This thread allows a secure bluetooth connection between two devices to
	 * be established.It runs until
	 * a connection is accepted (or until cancelled).
	 */
	private class AcceptThread extends Thread
	{
		// Local server socket
		private final BluetoothServerSocket	mmServerSocket;

		public AcceptThread( )
		{
			if(INFO) Log.i(TAG, "Constructing AcceptThread");

			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			BluetoothServerSocket tmp = null;
			// Create a new listening server socket
			try
			{
				// MY_UUID is the app's UUID string, also used by the client
				// code

				tmp = mBtAdapter.listenUsingRfcommWithServiceRecord(NAME, BASE_UUID);
				if(INFO) Log.i(TAG, "AcceptThread: bluetooth server socket = " + tmp);

			}
			catch(IOException e)
			{
				Log.e(TAG, "Listen Socket creation failed. listen()", e);

			}
			mmServerSocket = tmp;
		}

		public void run( )
		{
			if(DEBUG) Log.d(TAG, "BEGIN mAcceptThread" + this);

			setName("AcceptThread");

			BluetoothSocket socket = null;

			// Keep listening to the server socket if we are not not connected
			// or until exception occurs or a socket is returned

			while(mState != STATE_CONNECTED)
			{

				try
				{
					socket = mmServerSocket.accept( );
				}
				catch(IOException e)
				{
					Log.e(TAG, "accept() failed", e);
					break;
				}

				/*//@formatter:off
				 * If a connection was accepted, check in which state we are.
				 * STATE_LISTEN : do nothing
				 * STATE_CONNECTING : a client is trying to connect, start the connection manager thread
				 * STATE_NONE : do nothing
				 * STATE_CONNECTED : already connected, terminate the bluetooth server socket
				 *///@formatter:on
				if(socket != null)
				{
					if(DEBUG) Log.d(TAG, "socket != null | state: " + mState);
					synchronized(SHBluetoothNetworkManager.this)
					{
						switch(mState)
						{
							case STATE_LISTEN:
								if(INFO) Log.i(TAG, "Accept thread - STATE_LISTEN");

							case STATE_CONNECTING:
								if(INFO) Log.i(TAG, "Accept thread - STATE_CONNECTING");

								// Start the connected thread
								// A client is trying to establish connection
								connected(socket, socket.getRemoteDevice( ));
								break;
							case STATE_NONE:
								if(INFO) Log.i(TAG, "Accept thread - STATE_NONE");

							case STATE_CONNECTED:
								if(INFO) Log.i(TAG, "Accept thread - STATE_CONNECTED");

								// We may already be connected. So we terminate
								// the server socket, no point in it still
								// listening as we only want one connection

								try
								{
									socket.close( );
								}
								catch(Exception e)
								{
									Log.e(TAG, "Could not close socket", e);
								}
								break;
						}
					}

				}
			}

			if(INFO) Log.i(TAG, "END mAcceptThread" + this);

		}

		// Will cancel the listening socket, and cause the thread to finish
		public void cancel( )
		{
			try
			{
				mmServerSocket.close( );
			}
			catch(IOException e)
			{
				Log.e(TAG, "close() of server socket failed", e);
			}
		}
	}

	/**
	 * A connecting thread from the client side, this thread requires the
	 * information of a {@link BluetoothDevice}. This is obtained during
	 * {@link SHDeviceSelectActivity}. Once the {@link BluetoothDevice}
	 * information
	 * is received, we get a {@link BluetoothSocket} by calling
	 * {@link #createRfcommSocketToServiceRecord} with the same {@link UUID}
	 * used in
	 * the {@link AcceptThread} when calling
	 * {@link #listenUsingRfcommWithServiceRecord}. The thread attempts to
	 * connect
	 * on the {@link BluetoothSocket}, if able it resets and starts the
	 * {@link ConnectedThread}.
	 */
	private class ConnectThread extends Thread
	{
		private final BluetoothSocket	mmSocket;
		private final BluetoothDevice	mmDevice;

		public ConnectThread(BluetoothDevice device)
		{
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final

			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try
			{
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(BASE_UUID);
			}
			catch(IOException e)
			{
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run( )
		{
			if(INFO) Log.i(TAG, "BEGIN mConnectThread");

			setName("ConnectThread");

			try
			{
				// Connect the device through the socket. This will block until
				// it succeeds or throws an exception

				mmSocket.connect( );
			}
			catch(IOException connectException)
			{
				// Unable to connect; close the socket and get out
				Log.e(TAG, "unable to connect", connectException);
				try
				{
					mmSocket.close( );
				}
				catch(IOException closeException)
				{
					Log.e(TAG, "unable to close() socket during connection failure", closeException);
				}
				if(DEBUG) Log.d(TAG, "Unable to connect device.");
				// Try again
				exceptionManager(MSG_CONNECT_FAIL);
				return;
			}

			// Reset the thread when done
			synchronized(SHBluetoothNetworkManager.this)
			{
				if(DEBUG) Log.d(TAG, "Resetting mConnectThread");
				mConnectThread = null;
			}

			// Start the connected thread manager
			connected(mmSocket, mmDevice);

			if(INFO) Log.i(TAG, "END mConnectThread");
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel( )
		{
			try
			{
				mmSocket.close( );
			}
			catch(IOException e)
			{
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during an established connection with a remote device.
	 * It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread
	{
		private final BluetoothSocket	mmSocket;
		private final InputStream		mmInStream;
		private final OutputStream		mmOutStream;
		public String					input;

		public ConnectedThread(BluetoothSocket socket)
		{
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final

			try
			{
				tmpIn = socket.getInputStream( );
				tmpOut = socket.getOutputStream( );
			}
			catch(IOException e)
			{
				Log.e(TAG, "ConnectedThread: Failed to get input and output streams.");
				disconnect( );
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run( )
		{
			if(INFO) Log.i(TAG, "BEGIN mConnectedThread");

			setName("ConnectedThread"); // Does this change anything?

			byte[ ] buffer = new byte[1024]; // buffer store for the stream
			byte ch; 						// byte returned from read();
			int bytes; 						// bytes returned from read()

			// Keep listening to the InputStream until an exception occurs
			while(true)
			{
				try
				{

					bytes = 0;
					// read byte by byte until carriage return
					while((ch = (byte) mmInStream.read( )) != '\r')
					{
						buffer[bytes++] = ch;
					}

					// this method doesn't work, PIC sends multiple "buffers"
					// bytes = mmInStream.read(buffer);

					if(bytes > 0)
					{
						input = new String(buffer, "UTF-8").substring(0, bytes);
						// Send the input to the UI
						if(DEBUG) Log.d(TAG, "Received: " + input);
						sendMessage(MSG_READ, input);

					}

				}
				catch(IOException e)
				{
					Log.e(TAG, "Disconnected from device.", e);
					exceptionManager(MSG_CONNECTION_LOST); // TODO check which
															// is better
					// sendMessage(MSG_CANCEL, "Disconnected from device");
					break;
				}

				bBusy = false;
			}

			if(INFO) Log.i(TAG, "END mConnectedThread");

		}

		/**
		 * Call this from the main activity to send data to the remote device.
		 * 
		 * @param _data
		 *            The data to send.
		 * @return True if the data could be sent. <br>
		 *         False if the data could not be sent.
		 */
		public boolean write(/* byte[] */String _data)
		{
			try
			{
				if(_data != null)
				{
					sendMessage(MSG_WRITE, _data);
					if(DEBUG) Log.d(TAG, "Sending: " + _data);
					mmOutStream.write(_data.getBytes( ));
				}
				mmOutStream.write('\r');
				return true;
			}
			catch(IOException e)
			{
				Log.e(TAG, "Exception during write", e);
			}
			return false;
		}

		// Call this from the main activity to shutdown the connection
		public void cancel( )
		{
			if(DEBUG) Log.d(TAG, "ConnectedThread cancel() called.");

			try
			{
				mmSocket.close( );
			}
			catch(IOException e)
			{
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}

	}

	/**
	 * Synchronized method to send data to the connected device.
	 * 
	 * @param _out
	 *            The data to send out.
	 * @return True if the data could be sent.<br>
	 *         False if the data could not be sent.
	 */
	public boolean write(String _out)
	{
		ConnectedThread conThread;

		synchronized(this)
		{
			if(mState != STATE_CONNECTED)
			{
				// We are not connected so we can't write anything.
				sendMessage(MSG_NOT_CONNECTED, "Not connected to any device.");
				if(DEBUG) Log.d(TAG, "write() failed. not connected to anything");

				return false;
			}

			conThread = mConnectedThread;
		}

		return conThread.write(_out);
	}

	/**
	 * Send information the the UI concerning the possible errors the Bluetooth
	 * Manager encounters.
	 * Used to restart the connection threads if necessary.
	 */
	private void exceptionManager(int _errorDescription)
	{

		switch(_errorDescription)
		{
			case MSG_CONNECT_FAIL:
				sendMessage(_errorDescription, "Connection failed. Restarting.");
				if(DEBUG) Log.d(TAG, "Connection failed. Restarting.");
				break;
			case MSG_CONNECTION_LOST:
				sendMessage(_errorDescription, "Connection lost. Restaring.");
				if(DEBUG) Log.d(TAG, "Connection lost. Restaring.");
				break;
		}
		// Failed to connect, restart the service in order to try again
		SHBluetoothNetworkManager.this.start( );

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
		// It might happen that there's no activity handler, but here it doesn't
		// prevent application work flow
		if(mHandler != null)
		{
			mHandler.obtainMessage(type, value).sendToTarget( );
			// .obtainMessage(int what, int arg1, int arg2, Object obj)
		}
	}

	/**
	 * Sets the current active activity handler so messages could be sent.
	 * 
	 * @param _handler
	 *            The current activity handler
	 */
	public void setActivityHandler(Handler _handler)
	{
		mHandler = _handler;
	}

}
