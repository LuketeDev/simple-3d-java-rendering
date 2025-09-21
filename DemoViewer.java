import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class DemoViewer {

    public static void main(String[] args) {
        // Frame
        JFrame jFrame = new JFrame();
        Container pane = jFrame.getContentPane();
        pane.setLayout(new BorderLayout());

        // Slider Topo
        JSlider topSlider = new JSlider(0, 360, 180);
        pane.add(topSlider, BorderLayout.NORTH);

        // Slider Lateral
        JSlider sideSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(sideSlider, BorderLayout.EAST);

        // Slider inflate
        JSlider inflateSlider = new JSlider(0, 10, 0);
        pane.add(inflateSlider, BorderLayout.SOUTH);

        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                // Preencher janela
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Triângulos do tetraedro
                double SIZE = 100;
                List<Triangle> tris = new ArrayList<>();
                tris.add(new Triangle(
                        new Vertex(SIZE, SIZE, SIZE),
                        new Vertex(-SIZE, -SIZE, SIZE),
                        new Vertex(-SIZE, SIZE, -SIZE),
                        Color.WHITE));
                tris.add(new Triangle(
                        new Vertex(SIZE, SIZE, SIZE),
                        new Vertex(-SIZE, -SIZE, SIZE),
                        new Vertex(SIZE, -SIZE, -SIZE),
                        Color.RED));
                tris.add(new Triangle(
                        new Vertex(-SIZE, SIZE, -SIZE),
                        new Vertex(SIZE, -SIZE, -SIZE),
                        new Vertex(SIZE, SIZE, SIZE),
                        Color.GREEN));
                tris.add(new Triangle(
                        new Vertex(-SIZE, SIZE, -SIZE),
                        new Vertex(SIZE, -SIZE, -SIZE),
                        new Vertex(-SIZE, -SIZE, SIZE),
                        Color.BLUE));
                if (inflateSlider.getValue() > 0) {
                    for (int i = 0; i < inflateSlider.getValue(); i++) {
                        tris = inflate(tris);
                    }
                }

                // Valores
                double horizontalRot = Math.toRadians(topSlider.getValue());
                double verticalRot = Math.toRadians(sideSlider.getValue());

                // Transformações
                Matrix3 horizontalTransform = new Matrix3(new double[] {
                        Math.cos(horizontalRot), 0, -Math.sin(horizontalRot),
                        0, 1, 0,
                        Math.sin(horizontalRot), 0, Math.cos(horizontalRot)
                });
                Matrix3 verticalTransform = new Matrix3(new double[] {
                        1, 0, 0,
                        0, Math.cos(verticalRot), Math.sin(verticalRot),
                        0, -Math.sin(verticalRot), Math.cos(verticalRot)
                });
                Matrix3 transform = horizontalTransform.multiply(verticalTransform);

                g2.setColor(Color.WHITE);

                // Preenchedor do lado do triângulo
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                // Buffer
                double[] zBuffer = new double[img.getWidth() * img.getHeight()];
                for (int pixel = 0; pixel < zBuffer.length; pixel++) {
                    zBuffer[pixel] = Double.NEGATIVE_INFINITY;
                }
                for (Triangle triangle : tris) {
                    // Vértices
                    Vertex v1 = transform.transform(triangle.v1);
                    Vertex v2 = transform.transform(triangle.v2);
                    Vertex v3 = transform.transform(triangle.v3);

                    // Vértice normal para shading
                    Vertex ab = new Vertex(
                            v2.x - v1.x,
                            v2.y - v1.y,
                            v2.z - v1.z);

                    Vertex ac = new Vertex(
                            v3.x - v1.x,
                            v3.y - v1.y,
                            v3.z - v1.z);

                    Vertex normalVertex = new Vertex(
                            ab.y * ac.z - ab.z * ac.y,
                            ab.z * ac.x - ab.x * ac.z,
                            ab.x * ac.y - ab.y * ac.x);
                    double normalLength = Math.sqrt(normalVertex.x * normalVertex.x + normalVertex.y * normalVertex.y
                            + normalVertex.z * normalVertex.z);
                    normalVertex.x /= normalLength;
                    normalVertex.y /= normalLength;
                    normalVertex.z /= normalLength;

                    Vertex lightDir = new Vertex(1, -1, 1);
                    double lightLen = Math
                            .sqrt(lightDir.x * lightDir.x + lightDir.y * lightDir.y + lightDir.z * lightDir.z);
                    lightDir.x /= lightLen;
                    lightDir.y /= lightLen;
                    lightDir.z /= lightLen;

                    double dot = normalVertex.x * lightDir.x + normalVertex.y * lightDir.y
                            + normalVertex.z * lightDir.z;

                    double angleCos = Math.max(0, dot);

                    // Translates: Coordenada + meio do JPanel
                    // Move os triângulos para o centro
                    v1.x += getWidth() / 2;
                    v1.y += getHeight() / 2;
                    v2.x += getWidth() / 2;
                    v2.y += getHeight() / 2;
                    v3.x += getWidth() / 2;
                    v3.y += getHeight() / 2;

                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1,
                            Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() - 1,
                            Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    // Área do triangulo
                    double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                    // Baricentros
                    // Para y dentro de minY e maxY
                    for (int y = minY; y <= maxY; y++) {
                        // Para x dentro de minX e maxX
                        for (int x = minX; x <= maxX; x++) {
                            // Baricentros
                            double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;

                            // Baricentro entre 0 e 1 (Dentro do triangulo) ?
                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                // Profundidade do buffer
                                double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                int zIndex = y * img.getWidth() + x;
                                if (zBuffer[zIndex] < depth) {
                                    // Pintar triângulo
                                    img.setRGB(x, y, getShade(triangle.color, 0.2 + 0.8 * angleCos).getRGB());
                                    zBuffer[zIndex] = depth;
                                }
                            }
                        }
                    }
                    Path2D path = new Path2D.Double();
                    path.moveTo(v1.x, v1.y);
                    path.lineTo(v2.x, v2.y);
                    path.lineTo(v3.x, v3.y);
                    path.closePath();
                    g2.draw(path);
                }
                g2.drawImage(img, 0, 0, null);
            }
        };

        // Listeners
        topSlider.addChangeListener(e -> renderPanel.repaint());
        sideSlider.addChangeListener(e -> renderPanel.repaint());
        inflateSlider.addChangeListener(e -> renderPanel.repaint());

        pane.add(renderPanel, BorderLayout.CENTER);

        jFrame.setVisible(true);
        jFrame.setSize(400, 400);
    }

    public static Color getShade(Color color, double shade) {
        double redLinear = color.getRed() * shade;
        double greenLinear = color.getGreen() * shade;
        double blueLinear = color.getBlue() * shade;

        return new Color(
                (int) Math.floor(Math.min(255, redLinear)),
                (int) Math.floor(Math.min(255, greenLinear)),
                (int) Math.floor(Math.min(255, blueLinear)));
    }

    public static List<Triangle> inflate(List<Triangle> tris) {
        List<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vertex m1 = new Vertex((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2);
            Vertex m2 = new Vertex((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2);
            Vertex m3 = new Vertex((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2);

            result.add(new Triangle(t.v1, m1, m3, t.color));
            result.add(new Triangle(t.v2, m1, m2, t.color));
            result.add(new Triangle(t.v3, m2, m3, t.color));
            result.add(new Triangle(m1, m2, m3, t.color));
        }

        for (Triangle t : result) {
            for (Vertex v : new Vertex[] { t.v1, t.v2, t.v3 }) {
                double l = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z) / Math.sqrt(30000);
                v.x /= l;
                v.y /= l;
                v.z /= l;
            }
        }

        return result;
    }
}