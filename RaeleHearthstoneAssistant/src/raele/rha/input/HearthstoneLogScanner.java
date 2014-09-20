package raele.rha.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class HearthstoneLogScanner {
	
	private static final String CONFIG_FILENAME = "rhl_recorders.txt";
	private static final long TIMER_DELAY = 1000;
	private List<Recorder> chain;
	private int readLines;
	private String engineVersion;
	private String gfxDevice;
	private String desktop;
	private String virtual;
	private Properties direct3D;
	private List<LogEventListener> listeners;
	private String filename;
	private Timer timer;
	
	public HearthstoneLogScanner()
	{
		super();
		setup();
	}

	public HearthstoneLogScanner(String filename)
	throws FileNotFoundException
	{
		super();
		setup();
		
		if (!new File(filename).exists())
		{
			throw new FileNotFoundException(filename);
		}
		
		this.filename = filename;
	}
	
	private void setup()
	{
		this.readLines = 0;
		engineVersion = null;
		gfxDevice = null;
		direct3D = null;
		listeners = new LinkedList<LogEventListener>();
		FileInputStream configFile = null;
		Scanner scanner = null;
		
		try {
			configFile = new FileInputStream(CONFIG_FILENAME);
			scanner = new Scanner(configFile);
			this.chain = new LinkedList<Recorder>();
			
			while(scanner.hasNext())
			{
				String className = scanner.nextLine();
				try {
					Class<?> loadClass = Class.forName(className);
					if (Recorder.class.isAssignableFrom(loadClass))
					{
						Class<? extends Recorder> recorderClass = loadClass.asSubclass(Recorder.class);
						Recorder recorder = recorderClass.newInstance();
						chain.add(recorder);
//						System.out.println("Redorder " + recorderClass.getSimpleName() + " included to the chain.");
					}
					else
					{
						System.err.println("Class " + loadClass.getName() + " is not a Recorder.");
					}
				} catch (ClassNotFoundException e) {
					System.err.println("Class " + className + " was not found in classpath.");
				} catch (InstantiationException | IllegalAccessException e) {
					System.err.println("Couldn't instantiate recorder " + className + " because: " + e.getMessage());
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("File " + CONFIG_FILENAME + " was not found.");
			chain = new ArrayList<Recorder>(0);
		} finally {
			if (scanner != null) scanner.close();
			if (configFile != null) try {configFile.close();} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public void refresh()
	throws IllegalStateException, FileNotFoundException
	{
		if (this.filename == null)
		{
			throw new IllegalStateException("No file set.");
		}
		
		this.refresh(new FileInputStream(this.filename));
	}

	public void refresh(InputStream input) {
		Scanner scanner = new Scanner(input, "UTF-8");
		
		// Skips n lines already read
		if (this.readLines > 0)
		{
			//scanner.skip("(?:.*\\r?\\n|\\r){" + this.readLines + "}");
			for (int i = 0; i < this.readLines; i++)
			{
				scanner.nextLine();
			}
		}
		
		while (scanner.hasNext())
		{
			record(scanner);
		}
	}

	private void record(Scanner scanner) {
		Iterator<Recorder> iter = chain.iterator();
		boolean recorded = false;
		String line = scanner.nextLine();
		
		while(!recorded && iter.hasNext())
		{
			Recorder recorder = iter.next();
			recorded = recorder.record(line, this, scanner);
		}
		
		if (!recorded)
		{
			System.err.println("No suitable recorder for Line: " + line);
		}
		else
		{
			this.readLines++;
		}
	}

	public void recordEvent(LogEvent event) {
		for (LogEventListener listener : this.listeners)
		{
			try {
				listener.recordEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setReadLinesCount(int readLines) {
		this.readLines = readLines;
	}

	public int getReadLinesCount() {
		return readLines;
	}

	public Properties getDirect3D() {
		return direct3D;
	}

	public void setDirect3D(Properties direct3d) {
		direct3D = direct3d;
	}

	public String getEngineVersion() {
		return engineVersion;
	}

	public void setEngineVersion(String engineVersion) {
		this.engineVersion = engineVersion;
	}

	public String getGfxDevice() {
		return gfxDevice;
	}

	public void setGfxDevice(String gfxDevice) {
		this.gfxDevice = gfxDevice;
	}

	public String getDesktop() {
		return desktop;
	}

	public void setDesktop(String desktop) {
		this.desktop = desktop;
	}

	public String getVirtual() {
		return virtual;
	}

	public void setVirtual(String virtual) {
		this.virtual = virtual;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void addListener(LogEventListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeListener(LogEventListener listener)
	{
		this.listeners.remove(listener);
	}
	
	public void containsListener(LogEventListener listener)
	{
		this.listeners.contains(listener);
	}
	
	public void report(PrintStream stream) {
		stream.println("output_log.txt");
		stream.println("\tEngine version: " + this.getEngineVersion());
		stream.println("\tGfx Device: " + this.getGfxDevice());
		stream.println("\tDirect 3D: " + this.getDirect3D());
		stream.println("\tRead lines count: " + this.getReadLinesCount());
		stream.println("\tDesktop: " + this.getDesktop());
		stream.println("\tVirtual: " + this.getVirtual());
	}
	
	public void start()
	throws IllegalStateException, FileNotFoundException
	{
		if (this.filename == null)
		{
			throw new IllegalStateException("No file set.");
		}

		if (!new File(filename).exists())
		{
			throw new FileNotFoundException("File " + this.filename + " not found.");
		}
		
		this.timer = new Timer(this.getClass().getSimpleName());
		timer.schedule(new TimerTask() {
			@Override
			public void run()
			{
				try {
					HearthstoneLogScanner.this.refresh();
				} catch (IllegalStateException | FileNotFoundException e) {
					e.printStackTrace();
					HearthstoneLogScanner.this.timer.cancel();
					HearthstoneLogScanner.this.timer = null;
				}
			}
		}, TIMER_DELAY, TIMER_DELAY);
	}
	
	public void stop()
	{
		if (this.timer != null)
		{
			this.timer.cancel();
			this.timer = null;
		}
	}

}
