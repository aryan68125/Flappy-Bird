package com.aditya.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
	// batches renders textures and we cannot do collision detection directly on textures
	SpriteBatch batch;
	Texture background;
	//create a shapeRenderer for drawing shapes on the sprites of the game for collision detection
	ShapeRenderer shapeRenderer;

	//birds information
	Texture[] birds;
	int flapState = 0;
	//position of the bird
	float birdY = 0; //because here only y axis is the only axis that is changing
	//velocity of the bird in the game
	float velocity = 0;
	/*
	Drawing a circle around the bird for collision detection system for the game
	 */
	Circle birdCircle;

	//keeping track of the state of the game
	int gameState = 0;

	//pipes information
	Texture toptube;
	Texture bottomtube;
	float gap=400; //gap between pipes
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	//number of tubes
	float numberOfTubes = 4;
	float distanceBetweenTubes;
	//position of tube in x axis
	float[] tubeX = new float[(int) numberOfTubes];
	//since tubeOffset is going to be different for each tube so we are gonna require an offset array of type float
	float[] tubeOffset = new float[(int) numberOfTubes];
	/*
	Drawing a rectangles around the 4 tubes for collision detection system for the game
	 */
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangle;

	//scoring system for the game
	int score =0;
	//keep track of which tube is our active scorer
	int scoringTube = 0;
	//setting up the Font to display the score text in libGdx
	BitmapFont font;

	//GameOver texture for our game
	Texture gameOver;

	@Override
	public void create () {
		batch = new SpriteBatch(); //collection of sprites and it also helps manage them

		//setting up the background of the game
		background = new Texture("bg.png");

		//gameOver texture initialization
		gameOver = new Texture("gameover.png");

		//bird
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		//initialization of shape birdCircle on the bird texture on the screen
		birdCircle = new Circle();
		//initialization of shapeRenderer for drawing shapes on the sprites of the game for collision detection
		shapeRenderer = new ShapeRenderer();

		//pipes
		bottomtube = new Texture("bottomtube.png");
		toptube = new Texture("toptube.png");
		//maximum tube offset
		maxTubeOffset = Gdx.graphics.getHeight()/2-100-gap/2;
		//setting up randomGenerator here to randomly offset the pipes on the screen during the game
		randomGenerator  = new Random();
		//set up the distance between tubes on the screen in the game
		//here we are setting the distance between the tubes to be half the width of the screen
		distanceBetweenTubes = Gdx.graphics.getWidth()/2;
		/*
	       initializing the rectangles around the 4 tubes for collision detection system for the game
	    */
		topTubeRectangles = new Rectangle[(int) numberOfTubes];
		bottomTubeRectangle = new Rectangle[(int) numberOfTubes];

        //call the startGame() method to start the game as it initializes the default positions of the bird and the tubes
		startGame();

		//scoring mechanism
		//font initialization for displaying the score on the screen
		font = new BitmapFont();
		//setting up the characteristics of the font
		font.setColor(Color.WHITE); //color of the font
		font.getData().setScale(10); //size of the font
	}

	public void startGame()
	{
		//reset the position of the bird in y axis
		//defining the y axis of the bird
		birdY = Gdx.graphics.getHeight()/2-birds[flapState].getHeight()/2; //inital y position of the bird

		//reset the positions of the tubes again
		//setting up aultiple tubes for the display on the screen
		for( int i =0; i<numberOfTubes;i++)
		{
			//initially for each of the 4 tubes setup the x coordinate and the offset
			//code to randomly generate pipes of different heights
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 900); //nextFloat will create a random number between 0 and 1
			//initial position of 4 tube
			//making tubes appear on the right hand side of the screen
			tubeX[i] = Gdx.graphics.getWidth()/2-toptube.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			//initializing the rectangles for each of the tubes in the array
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin(); //begin rendering sprites
		batch.draw(background, 0, 0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //displaying the background image full screen start rendering from bottom left of the screen

		//logic to animate bird in the game by making flapState to oscillate between 1 and 0 alternatively
		if(flapState==0)
		{
			for(int i =0;i<10000000;i++)
			{
				if (i==9999999)
				{
					flapState=1;
				}
			}
		}
		else
		{
			for(int j =0;j<10000000;j++)
			{
				if (j == 9999999)
				{
					flapState = 0;
				}
			}
		}

		//keeping track of game state
		if(gameState==1) //gameState = state of playing the game gameState = 1
		{
			//scoring mechanism for the game
			//increase the score by one if the tube pass the center of the screen and the player is not dead yet
			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2)
			{
				score ++;
				//log the score each time
				Gdx.app.log("Score", String.valueOf(score));
				if (scoringTube < numberOfTubes-1)
				{
					scoringTube++;
				}
				else
				{
					scoringTube = 0;
				}
			}

			//stopping the bird from falling out of bounds from the devices screen
			//if (birdY>0 || velocity <0) gives you ability to move the bird with a tap after the bird has touched the bottom of the screen
			if (birdY>0) //does not gives you the ability to move the bird with a tap after the bird has touched the bottom of the screen
			{
				//velocity of the flappy bird
				velocity ++;
				//y position of the flappy bird EMULATING gravity in the game
				birdY -= velocity;
			}
			else //if the bird is not above the bottom of the screen then the gameState is 2
			{
				//set the gameState = 2 which is the state where the player died and the high score is shown to the player after the player is dead
				gameState = 2;
			}

			//input for flappy bird
			if(Gdx.input.justTouched()) //Gdx.input.justTouched() is called every time the screen is tapped
			{
				velocity = -20; //subtracting the velocity will add in the height in the y axis of the bird in the game and the bird will shoot up in the air
			}

			//display the 4 pipes using the forloop on the screen with their respective animations
			for( int i =0; i<numberOfTubes;i++)
			{
				//in order to to make infinite number of tubes moving across the screen from right to left
				//do a check on the tubes if they go out of the screen from the left of the screen then send them back to the right of the screen
				if(tubeX[i]<-toptube.getWidth())
				{
					tubeX[i] += numberOfTubes * distanceBetweenTubes; // formula to shift the tubes from left to right as soon as the tube goes out of bounds from the screen
					//reset the tube off set whenever a tube moves to the right of the screen
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 900); //nextFloat will create a random number between 0 and 1
				}
				else
				{
					//animation of tubes in the game movement from right to left
					tubeX[i] = tubeX[i]-tubeVelocity;
				}

				//pipes
				batch.draw(toptube,tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i]);
				batch.draw(bottomtube,tubeX[i],Gdx.graphics.getHeight()/2 - gap/2 - bottomtube.getHeight() + tubeOffset[i]);

				//adding shape rectangles to each of the 4 tubes for collision detection in the same positions as the tubes x and y coordinate
				topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], toptube.getWidth(),toptube.getHeight());
				bottomTubeRectangle[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 - gap/2 - bottomtube.getHeight() + tubeOffset[i], bottomtube.getWidth(),bottomtube.getHeight());
			}
		}
		else if(gameState == 0)//GameState = Very beginning  of the game state =0
		{
			//input for flappy bird
			if(Gdx.input.justTouched()) //Gdx.input.justTouched() is called every time the screen is tapped
			{
				Gdx.app.log("Touched","Yes more I want more");
				gameState = 1;
			}
		}
		else if (gameState == 2)//GameState = The Player has died gameState=2
		{
			//what happens when the player is dead
			//draw the gameOver message on the screen here
			batch.draw(gameOver, Gdx.graphics.getWidth()/2-gameOver.getWidth()/2, Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);

			//input for flappy bird
			//if user touches the screen set GameState = 1
			if(Gdx.input.justTouched()) //Gdx.input.justTouched() is called every time the screen is tapped
			{
				Gdx.app.log("Touched","Yes more I want more");
				gameState = 1;

				//reset the bird and tubes positions on the screen when the player dies
				startGame();

				//reset the score of the player back to 0
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		//bird
		batch.draw(birds[flapState],Gdx.graphics.getWidth()/2-birds[flapState].getWidth()/2,birdY); //rendering bird at the center of the screen
		//display score
		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth()/2-Gdx.graphics.getWidth()/3-130,Gdx.graphics.getHeight()/2+Gdx.graphics.getHeight()/3+260);
		batch.end();
		//create a circle that overlaps where the bird is for collision detection
		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2); //set the shape which is our bird circle to match the position of the bird on the screen

//      Rendering the birdCircle shape for collision detection on the screen for testing purposes
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.BLUE);
//	    shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

		for( int i =0; i<numberOfTubes;i++)
		{
			//rendering the recangle shapes on the tubes on the screen for testing purposes
//			shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], toptube.getWidth(),toptube.getHeight());
//			shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 - gap/2 - bottomtube.getHeight() + tubeOffset[i], bottomtube.getWidth(),bottomtube.getHeight());

			//because we need a loop anyway to check if the shape circle of the bird collides with the rectangle shape of the tubes hence we are doing the collision detection check here
			//Logic for collision detection system
			if(Intersector.overlaps(birdCircle , topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i]))
			{
				//what happens when a collision is detected
               Gdx.app.log("Collision", "Collision detected!!!!!!!");
			   //set the gameState = 2 which is the state where the player died and the high score is shown to the player after the player is dead
				gameState = 2;
			}
		}
//		shapeRenderer.end();
	}
	
//	@Override
//	public void dispose () {
//		batch.dispose();
//		background.dispose();
//	}
}
