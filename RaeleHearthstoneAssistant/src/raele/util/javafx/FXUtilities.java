package raele.util.javafx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
 
/**
 * General JavaFX utilities
 * 
 * @author hendrikebbers
 * 
 */
public class FXUtilities {
	
	/**
	 * Generic callback interface for dealing with events that don't have a
	 * return, since javafx.util.Callback class doesn't support such behavior.
	 *
	 * @param <T> Parameter the callback expects to receive.
	 */
	public static interface VoidCallback<T> {
		public void call(T t);
	}
 
    /**
     * Simple helper class.
     * 
     * @author hendrikebbers
     */
    private static class ThrowableWrapper {
        Throwable t;
    }
 
    /**
     * Invokes a Runnable in JFX Thread and waits while it's finished. Like
     * SwingUtilities.invokeAndWait does for EDT.
     * 
     * @param run
     *            The Runnable that has to be called on JFX thread.
     * @throws InterruptedException
     *             f the execution is interrupted.
     * @throws ExecutionException
     *             If a exception is occurred in the run method of the Runnable
     */
    public static void runAndWait(final Runnable run)
            throws InterruptedException, ExecutionException {
        if (Platform.isFxApplicationThread()) {
            try {
                run.run();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        } else {
            final Lock lock = new ReentrantLock();
            final Condition condition = lock.newCondition();
            final ThrowableWrapper throwableWrapper = new ThrowableWrapper();
            lock.lock();
            try {
                Platform.runLater(new Runnable() {
 
                    @Override
                    public void run() {
                        lock.lock();
                        try {
                            run.run();
                        } catch (Throwable e) {
                            throwableWrapper.t = e;
                        } finally {
                            try {
                                condition.signal();
                            } finally {
                                lock.unlock();
                            }
                        }
                    }
                });
                condition.await();
                if (throwableWrapper.t != null) {
                    throw new ExecutionException(throwableWrapper.t);
                }
            } finally {
                lock.unlock();
            }
        }
    }
    
    private static class ChangeImageHandler implements EventHandler<Event> {
    	
    	private Image image;
		private ImageView view;

		public ChangeImageHandler(ImageView view, String filename)
    	throws IOException
    	{
			this.view = view;
    		this.image = new Image("file:" + filename);
    	}

		@Override
		public void handle(Event event) {
			this.view.setImage(image);
		}
    	
    }
    
    public static void hoverConfigOrThrow(ImageView view, String normal,
    		String hover, String pressed) throws IOException
    {
    	view.setOnMouseEntered(new ChangeImageHandler(view, hover));
    	view.setOnMouseExited(new ChangeImageHandler(view, normal));
    	
    	view.setOnMousePressed(new ChangeImageHandler(view, pressed));
    	view.setOnMouseReleased(new ChangeImageHandler(view, normal));
    }
    
    public static void hoverConfigOrHandle(ImageView view, String normal,
    		String hover, String pressed, VoidCallback<IOException> handler)
    {
    	try {
    		view.setOnMouseEntered(new ChangeImageHandler(view, hover));
    	} catch (IOException e) {
    		handler.call(e);
    	}
    	
    	try {
    		view.setOnMouseExited(new ChangeImageHandler(view, normal));
    	} catch (IOException e) {
    		handler.call(e);
    	}
    	
    	try {
    		view.setOnMousePressed(new ChangeImageHandler(view, pressed));
    	} catch (IOException e) {
    		handler.call(e);
    	}
    	
    	try {
    		view.setOnMouseReleased(new ChangeImageHandler(view, normal));
    	} catch (IOException e) {
    		handler.call(e);
    	}
    }
    
    public static void hoverConfigOrUndo(ImageView view, String normal,
    		String hover, String pressed)
    {
    	try {
    		view.setOnMouseEntered(new ChangeImageHandler(view, hover));
    		view.setOnMouseExited(new ChangeImageHandler(view, normal));
    		
    		view.setOnMousePressed(new ChangeImageHandler(view, pressed));
    		view.setOnMouseReleased(new ChangeImageHandler(view, normal));
    	} catch (IOException e) {}
    }
    
}