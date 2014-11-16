package fr.jcgay.notification;

import com.google.auto.value.AutoValue;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@AutoValue
public abstract class Icon {

    public abstract String id();

    abstract URL content();

    public RenderedImage toRenderedImage() {
        try {
            return ImageIO.read(content());
        } catch (IOException e) {
            throw new SendNotificationException("Error while reading status icon.", e);
        }
    }

    public byte[] toByteArray() {
        InputStream is = null;
        try {
            is = content().openStream();
            return ByteStreams.toByteArray(is);
        } catch (IOException e) {
            throw new SendNotificationException("Error while reading status icon.", e);
        } finally {
            Closeables.closeQuietly(is);
        }
    }

    public String asPath() {
        String folder = System.getProperty("java.io.tmpdir") + "/send-notifications-icons/";
        String extension = content().getPath().substring(content().getPath().lastIndexOf(".") + 1);
        File icon = new File(folder + id() + "." + extension);
        if (!icon.exists()) {
            new File(folder).mkdirs();
            try {
                ImageIO.write(toRenderedImage(), extension, icon);
            } catch (IOException e) {
                throw new SendNotificationException("Can't write notification icon icon: " + icon.getPath(), e);
            }
        }
        return icon.getPath();
    }

    public static Icon create(URL content, String id) {
        return new AutoValue_Icon(id, content);
    }
}
