import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.JPanel;


class Renderer extends JPanel //This class is used to paint on the frame
{

	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		FlappyBird.flappyBird.repaint(g); //calling the repaint() of FlappyBird class
	}
	
}


public class FlappyBird implements ActionListener, MouseListener, KeyListener
{

	public static FlappyBird flappyBird;  //object of FlappyBird class declared

	public final int WIDTH = 800, HEIGHT = 800; //Height and Width of the frame is set

	public Renderer renderer; //object of Renderer class is declared

	public Rectangle bird; //object of Rectangle class is declared

	public ArrayList<Rectangle> columns;  //ArrayList<> is a collection which is used here to declare ArrayList of Rectangle class

	public int ticks, yMotion, score;

	public boolean gameOver, started; 

	public Random rand; //object of Random class is declared

	public FlappyBird() //Constructor of FlappyBird class
	{
		JFrame jframe = new JFrame();  // object of JFrame is defined 
		Timer timer = new Timer(30, this); // object of Timer is defined and it's Constructor is initialized

		renderer = new Renderer(); //renderer object is defined
		rand = new Random(); //random object is defined

		jframe.add(renderer); //object of JPanel is added to JFrame (Panel is added to Frame)
		jframe.setTitle("Flappy Bird"); //Title of the Frame
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Action when Clicking the Close button
		jframe.setSize(WIDTH, HEIGHT); //Width And Height of the Frame is set
		jframe.addMouseListener(this); //MouseListener is added to the Frame
		jframe.addKeyListener(this);  //KeyListener is added to the Frame
		jframe.setResizable(false); //Frame is no Resizabel
		jframe.setVisible(true); //Frame is Visible ( Else what's the point of Creating this program ;) ) 

		bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); //Rectangle is used to define the bird
		columns = new ArrayList<Rectangle>(); //object of ArrayList is defined

		addColumn(true); //addColumn is called
		addColumn(true); //these adds columns to the window
		addColumn(true);
		addColumn(true);

		timer.start(); //timer is started (it will be used to repaint the panel)
	}  

	public void addColumn(boolean start)
	{
		int space = 300;
		int width = 100;
		int height = 50 + rand.nextInt(300);

		if (start)
		{
			columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
		}
		else
		{
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
		}
	}

	public void paintColumn(Graphics g, Rectangle column)  //paints the green blocks of the game
	{
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	public void jump() //jump action of the bird is defined here
	{
		if (gameOver) //if game is over
		{
			bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); //initialize everything for next round
			columns.clear();
			yMotion = 0; 
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameOver = false;
		}

		if (!started) //if not started
		{
			started = true; //then start
		}
		else if (!gameOver)  //else if game not over but game started
		{
			if (yMotion > 0)  //if bird is going down
			{
				yMotion = 0;
			}

			yMotion -= 10;  //move the bird up by 10 pixel
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)  //this sets the left movement of the blocks and right movement of the bird 
	{
		int speed = 10;

		ticks++;

		if (started)
		{
			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMotion < 15)
			{
				yMotion += 2; 
			}

			for (int i = 0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);

				if (column.x + column.width < 0)
				{
					columns.remove(column);

					if (column.y == 0)
					{
						addColumn(false);
					}
				}
			}

			bird.y += yMotion;

			for (Rectangle column : columns)
			{
				if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10)
				{
					score++;
				}

				if (column.intersects(bird))
				{
					gameOver = true;

					if (bird.x <= column.x)
					{
						bird.x = column.x - bird.width;

					}
					else
					{
						if (column.y != 0)
						{
							bird.y = column.y - bird.height;
						}
						else if (bird.y < column.height)
						{
							bird.y = column.height;
						}
					}
				}
			}

			if (bird.y > HEIGHT - 120 || bird.y < 0)
			{
				gameOver = true;
			}

			if (bird.y + yMotion >= HEIGHT - 120)
			{
				bird.y = HEIGHT - 120 - bird.height;
				gameOver = true;
			}
		}

		renderer.repaint();
	}

	public void repaint(Graphics g)  //this function actually does the painting stuff
	{
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT); //to set the background color

		g.setColor(Color.green);
		g.fillRect(0, HEIGHT - 120, WIDTH, 20); //to set the grass

		g.setColor(Color.red);
		g.fillRect(bird.x, bird.y, bird.width, bird.height); //to set the bird

		for (Rectangle column : columns) //enhanced for loop 
		{
			paintColumn(g, column); //calling paintColumn to paint the green blocks
		}

		g.setColor(Color.white); 
		g.setFont(new Font("Arial", 1, 100)); //sets text for the game

		if (!started)
		{
			g.drawString("Click to start!", 75, HEIGHT / 2 - 50);  //if game is not started print Click to start
		}

		if (gameOver)
		{
			g.drawString("Game Over!", 100, HEIGHT / 2 - 50); //if gameOver = 1 print game over
		}

		if (!gameOver && started)
		{
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100); //if game is not over(gameOver=0) and game is started(started=1) print the score 
		}
	}

	public static void main(String[] args) //main
	{
		flappyBird = new FlappyBird(); //Calling the constructor of FlappyBird(main) class
	}

	@Override
	public void mouseClicked(MouseEvent e) //When mouse is clicked
	{
		jump();							   //jump() is called
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE) //when Space is clicked 
		{
			jump();								//jump() is called
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{

	}

}
