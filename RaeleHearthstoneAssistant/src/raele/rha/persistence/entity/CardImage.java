package raele.rha.persistence.entity;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class CardImage {

	private Long id;
	private byte[] image;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Lob
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public static BufferedImage getBufferedImage(CardImage image)
	{
		BufferedImage result;
		
		try {
			byte[] binary = image.getImage();
			ByteArrayInputStream input = new ByteArrayInputStream(binary);
			result = ImageIO.read(input);
		} catch (IOException e) {
			result = null;
		}
		
		return result;
	}
	public static void setBufferedImage(BufferedImage buff, CardImage image)
	{
		byte[] result;
		
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			if (ImageIO.write(buff, "png", output))
			{
				result = output.toByteArray();
			}
			else
			{
				throw new IOException();
			}
		} catch (IOException e) {
			result = null;
		} 
		
		image.setImage(result);
	}

}
