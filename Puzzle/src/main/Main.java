package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class ImagePart {

	private BufferedImage imageSource;
	private int number;

	public ImagePart(BufferedImage imageSource, int number) {
		this.imageSource = imageSource;
		this.number = number;
	}

	public BufferedImage getImageSource() {
		return imageSource;
	}

	public void setImageSource(BufferedImage imageSource) {
		this.imageSource = imageSource;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "ImagePart [imageSource=" + imageSource + ", number=" + number + "]";
	}

}

class PuzzlePanel extends JPanel implements MouseListener, ActionListener {

	private int interval = 0;
	private int imageWidth, imageHeight;
	private int imageWidthPart, imageHeightPart;
	private String imagePath = "/images/puzzleImage.jpg";
	private BufferedImage imageOri;
	private ImagePart imagePart[][] = new ImagePart[5][5];
	private static final int dx[] = { 0, 0, -1, 1 };
	private static final int dy[] = { -1, 1, 0, 0 };
	private int attempt;
	private static final int INTERVAL_X = 120;
	private static final int INTERVAL_Y = 30;
	private JButton restartButton;
	private BufferedImage miniImage;
	private Image backgroundImage;

	PuzzlePanel() {
		setLayout(null);
		addMouseListener(this);
		try {
			imageOri = ImageIO.read(this.getClass().getResource(imagePath));
			imageWidth = imageOri.getWidth();
			imageHeight = imageOri.getHeight();
			// System.out.println(imageWidth + " " + imageHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String file = imagePath.substring(imagePath.lastIndexOf("/") + 1);
		file = file.substring(0, file.indexOf("."));
		// System.out.println(file);
		imageWidthPart = imageWidth / 4;
		imageHeightPart = imageHeight / 4;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				BufferedImage bufferedImage = new BufferedImage(imageWidthPart, imageHeightPart,
						BufferedImage.TYPE_INT_BGR);
				bufferedImage.createGraphics().drawImage(imageOri, 0, 0, imageWidthPart, imageHeightPart,
						imageWidthPart * j, imageHeightPart * i, imageWidthPart * j + imageWidthPart,
						imageHeightPart * i + imageHeightPart, this);
				Integer number = i * 4 + j;
				String filename = file + number.toString() + ".jpg";
				// System.out.println("filename : " + filename);
				String outFile = this.getClass().getResource("/images/").toString().substring(6) + filename;
				try {
					ImageIO.write(bufferedImage, "jpg", new File(outFile));
					imagePart[i][j] = new ImagePart(ImageIO.read(new File(outFile)), number);
					// System.out.println(imagePart[i][j]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		getMiniImage();
	
		
		imagePart[0][0] = null;
		randomSetPart();
		requestFocus();

		restartButton = new JButton("재시작");
		restartButton.setBounds(610, 640, 120, 40);
		restartButton.addActionListener(this);
		add(restartButton);

		URL url = this.getClass().getResource("/images/backgroundImage.jpg");
		backgroundImage = new ImageIcon(url).getImage();

	}

	@Override
	public boolean isFocusTraversable() {
		return true;
	}
	
	private void getMiniImage(){
		miniImage = new BufferedImage(imageWidthPart, imageHeightPart,
				BufferedImage.TYPE_INT_BGR);
		miniImage.createGraphics().drawImage(imageOri, 0, 0, imageWidth / 4, imageHeight / 4,
				0, 0, imageWidth, imageHeight, this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		rendering((Graphics2D) g);
		g.drawImage(backgroundImage, 0, 0, null);
		g.drawImage(miniImage, INTERVAL_X * 8 + 10, INTERVAL_Y + 30, null);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				g.drawRect(imageWidthPart * i + INTERVAL_X, imageHeightPart * j + INTERVAL_Y, imageWidthPart,
						imageHeightPart);
				if (imagePart[i][j] != null) {
					g.drawImage(imagePart[i][j].getImageSource(), imageWidthPart * j + INTERVAL_X,
							imageHeightPart * i + INTERVAL_Y, this);
				}
			}
		}
		g.setColor(Color.WHITE);
		setFont(new Font("serif", Font.BOLD, 15));
		g.drawString("Attempt : " + attempt, INTERVAL_X + 200, 665);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int part = getClickedPart(e.getX(), e.getY());
		if (part != -1) {
			int xx = part / 4;
			int yy = part % 4;
			// System.out.println(imagePart[xx][yy]);
			movePart(xx, yy);
			if (checkGameEnd()) {
				JOptionPane.showMessageDialog(this, "게임이 끝났습니다! 총 시도 횟수 : " + attempt, "게임 종료!",
						JOptionPane.INFORMATION_MESSAGE);
				randomSetPart();
				attempt = 0;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void setTopLeftEmpty() {
		for (int i = 3; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				if (imagePart[i][j] == null) {
					for (int k = 0; k < 4; k += 2) {
						int xx = i + dx[k];
						int yy = j + dy[k];
						if (xx >= 0 && yy >= 0 && xx < 4 && yy < 4) {
							swapPart(i, j, xx, yy);
						}
					}
				}
			}
		}
	}

	private int getClickedPart(int x, int y) {
		// System.out.println(x + " " + y);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (imageWidthPart * j + INTERVAL_X <= x && x <= imageWidthPart * j + imageWidthPart + INTERVAL_X
						&& imageHeightPart * i + INTERVAL_Y <= y
						&& y <= imageHeightPart * i + imageHeightPart + INTERVAL_Y) {
					return i * 4 + j;
				}
			}
		}
		return -1;
	}

	private void movePart(int x, int y) {
		for (int k = 0; k < 4; k++) {
			int xx = x + dx[k];
			int yy = y + dy[k];
			if (xx >= 0 && yy >= 0 && xx < 4 && yy < 4 && imagePart[xx][yy] == null) {
				swapPart(x, y, xx, yy);
				attempt++;
				break;
			}
		}
	}

	private void swapPart(int x1, int y1, int x2, int y2) {
		if (x1 == x2 && y1 == y2)
			return;
		ImagePart tmp = imagePart[x1][y1];
		imagePart[x1][y1] = imagePart[x2][y2];
		imagePart[x2][y2] = tmp;
		repaint();
	}

	private void randomSetPart() {
		int nextX = 0;
		int nextY = 0;
		for (int i = 0; i < 2000; i++) {
			int xx = nextX + dx[(int) (Math.random() * 4)];
			int yy = nextY + dy[(int) (Math.random() * 4)];
			if (xx >= 0 && yy >= 0 && xx < 4 && yy < 4) {
				swapPart(nextX, nextY, xx, yy);
				nextX = xx;
				nextY = yy;
			}
		}
		setTopLeftEmpty();
	}


	

	private boolean checkGameEnd() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (imagePart[i][j] != null)
					if (imagePart[i][j].getNumber() != i * 4 + j)
						return false;
				if (imagePart[i][j] == null && (i != 0 || j != 0))
					return false;
			}
		}
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == restartButton) {
			int yes = JOptionPane.showConfirmDialog(this, "다시 시작하시겠습니까?", "확인", JOptionPane.OK_OPTION);
			if(yes == JOptionPane.OK_OPTION) {
				attempt = 0;
				randomSetPart();
			}
		}
	}

	private void rendering(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

}

class MainFrame extends JFrame {
	
	Image iconImage = new ImageIcon(this.getClass().getResource("/images/icon.png")).getImage();

	MainFrame() {
		setSize(1280, 720);
		setResizable(false);
		setTitle("Puzzle");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(new PuzzlePanel());
		setIconImage(iconImage);
	}

}

public class Main {
	public static void main(String[] args) {
		JFrame myFrame = new MainFrame();
		myFrame.setVisible(true);
	}
}
