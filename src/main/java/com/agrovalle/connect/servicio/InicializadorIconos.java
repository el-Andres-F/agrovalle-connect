package com.agrovalle.connect.servicio;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Genera los iconos JPG de perfil, carrito y cierre de sesión para la web. */
@Component
public class InicializadorIconos implements ApplicationRunner {

    private static final int SIZE = 128;
    private static final Color INK = new Color(59, 51, 40);

    @Override
    public void run(ApplicationArguments args) throws IOException {
        Path folder = Path.of("src", "main", "resources", "static", "iconos");
        if (!Files.isDirectory(Path.of("src", "main", "resources", "static"))) {
            folder = Path.of("target", "classes", "static", "iconos");
        }
        Files.createDirectories(folder);
        generate(folder.resolve("usuario.jpg"), this::drawUser);
        generate(folder.resolve("carrito.jpg"), this::drawCart);
        generate(folder.resolve("salir.jpg"), this::drawLogout);
    }

    private void generate(Path path, IconPainter painter) throws IOException {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(250, 245, 236));
        graphics.fillRect(0, 0, SIZE, SIZE);
        graphics.setColor(INK);
        graphics.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        painter.paint(graphics);
        graphics.dispose();
        ImageIO.write(image, "jpg", path.toFile());
    }

    private void drawUser(Graphics2D graphics) {
        graphics.draw(new Ellipse2D.Double(46, 17, 36, 36));
        graphics.draw(new Ellipse2D.Double(27, 55, 74, 58));
        graphics.setColor(new Color(250, 245, 236));
        graphics.fillRect(20, 84, 90, 30);
        graphics.setColor(INK);
        graphics.draw(new Line2D.Double(28, 85, 28, 88));
        graphics.draw(new Line2D.Double(100, 85, 100, 88));
        graphics.draw(new Line2D.Double(28, 113, 100, 113));
    }

    private void drawCart(Graphics2D graphics) {
        graphics.draw(new Line2D.Double(15, 24, 29, 24));
        graphics.draw(new Line2D.Double(29, 24, 42, 83));
        graphics.draw(new RoundRectangle2D.Double(40, 39, 68, 43, 4, 4));
        graphics.draw(new Line2D.Double(43, 54, 105, 54));
        graphics.draw(new Line2D.Double(46, 69, 108, 69));
        graphics.draw(new Ellipse2D.Double(48, 91, 14, 14));
        graphics.draw(new Ellipse2D.Double(89, 91, 14, 14));
    }

    private void drawLogout(Graphics2D graphics) {
        graphics.draw(new RoundRectangle2D.Double(18, 15, 53, 98, 10, 10));
        graphics.draw(new Line2D.Double(51, 64, 111, 64));
        graphics.draw(new Line2D.Double(87, 40, 111, 64));
        graphics.draw(new Line2D.Double(87, 88, 111, 64));
        graphics.draw(new Line2D.Double(50, 33, 50, 44));
        graphics.draw(new Line2D.Double(50, 84, 50, 95));
    }

    @FunctionalInterface
    private interface IconPainter {
        void paint(Graphics2D graphics);
    }
}
