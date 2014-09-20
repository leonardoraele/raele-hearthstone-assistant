package raele.util.swing;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JWindow;

public class LoadingWindow {
	
	private static JWindow loading;
	
	public static void show()
	{
		LoadingWindow.hide();
		loading = new JWindow();
		loading.setLocationRelativeTo(null);
		Point location = loading.getLocation();
		Dimension size = loading.getSize();
		loading.setLocation(location.x - size.width/2, location.y - size.height/2);
		loading.add(new JLabel("Raele Hearthstone Assistant is being loaded. Please wait..."));
		loading.pack();
		loading.setVisible(true);
	}

	public static void hide() {
		if (loading != null)
		{
			loading.dispose();
		}
	}

}
