import java.io.File;
import java.util.ArrayList;

import processing.core.*;

public class Main extends PApplet
{
	NeuralNetwork nn;
	
	boolean mouseWasDown;
	ArrayList<Button> buttons;
	PGraphics canvas;	
	PImage actualImage;
	String results;
		
	public void settings() { size(800,600); }

	public void createAndTrainNN()
	{
		// create neural network with 400 inputs, 300 hidden nodes, and 10 outputs
		nn = new NeuralNetwork();
		nn.addLayer(400);
		nn.addLayer(300);
		nn.addLayer(10);
		
		System.out.println();
		for (ArrayList<Perceptron> layer : nn.layers){
			for (Perceptron P : layer){
				P.print(" ");
			}
		}

		// learn from every image in the images folder
		File imagesDir = new File("images");
		String[] filenames = imagesDir.list();
		for(int i=0;i<filenames.length;i++)
		{
			String labelString = filenames[i].split("_")[2].split("\\.")[0];
			int label = Integer.parseInt(labelString);
			PImage image = loadImage("images/"+filenames[i]);
			image.updatePixels();
			double[] inputs = new double[image.pixels.length];
			for(int j=0;j<image.pixels.length;j++)
			{
				inputs[j] = (brightness(image.pixels[j])/255.0);
				//System.out.println("INPUT#" + j + ": " + inputs[j]);
			}
			double[] outputs = new double[10];
			for(int j=0;j<10;j++)
				outputs[j] = (label==j) ? 1.0 : 0.0;
			// train with the current image data
			nn.train(inputs, outputs);
			// display progress after training on every 1000 images
			if(i%1000==0) System.out.println("PROGRESS: " + i + "/" + filenames.length);
		}

		System.out.println();
		for (ArrayList<Perceptron> layer : nn.layers){
			for (Perceptron P : layer){
				P.print(" ");
			}
		}
	}
	
	public void testNN(PImage image)
	{
		// when image is null, pick a random image from images folder
		if(image == null)
		{
			File imagesDir = new File("images");
			String[] filenames = imagesDir.list();
			String filename = filenames[(int)(Math.random()*filenames.length)];
			image = loadImage("images/"+filename);
			actualImage = image;
		}
		image.updatePixels();
		double[] inputs = new double[image.pixels.length];
		for(int j=0;j<image.pixels.length;j++)
			inputs[j] = (brightness(image.pixels[j])/255.0);
		// get prediction from neural network
		double[] outputs = nn.predict(inputs);
		// convert prediction into results string
		int prediction = 0;
		results = "RESULTS:\n";
		for(int i=1;i<outputs.length;i++)
		{
			results += i + ": " + outputs[i] + "\n";
			if(outputs[i] > outputs[prediction])
				prediction = i;
		}
		results += " \nPREDICTION: " + prediction;
	}
	
	public void setup()
	{				
		createAndTrainNN();

		// set processing draw modes
		textSize(24);
		textAlign(CENTER,CENTER);
		imageMode(CENTER);
		rectMode(CENTER);

		mouseWasDown = true;
		
		buttons = new ArrayList<Button>();
		buttons.add(new Button("CLEAR",600,64));
		buttons.add(new Button("CUSTOM",600,112));
		buttons.add(new Button("RANDOM",600,160));
		
		// canvas is what the user draws on
		canvas = createGraphics(400,400);		
		canvas.beginDraw();
		canvas.stroke(255);
		canvas.strokeWeight(24);
		canvas.background(0);
		canvas.endDraw();
		// actual image contains data set to test neural network
		actualImage = createImage(20,20,RGB);
		results = "";
	}
	
	public void draw()
	{
		boolean mouseClick = mousePressed && !mouseWasDown;
		mouseWasDown = mousePressed;
		
		// handle user input
		canvas.beginDraw();
		boolean mouseOverButton = false;
		if(mouseClick)
			for(Button b : buttons)
				if(b.mouseOverButton)
				{
					mouseOverButton = true;
					switch(b.label)
					{
					case "CLEAR":
						canvas.background(0);
						break;
					case "CUSTOM":					
						testNN(actualImage);
						break;
					case "RANDOM":
						testNN(null);
						break;					
					}
				}
		if(mousePressed && !mouseOverButton)
		{
			canvas.line(mouseX-100,mouseY-100,pmouseX-100,pmouseY-100);
			actualImage = canvas.copy();
			actualImage = cropAndResize(actualImage);
		}
		canvas.endDraw();
		
		// clear background
		background(127);
		// draw prompt
		textSize(24); fill(255); stroke(0);
		text("Draw a number here:",width/2-100,64);
		// draw buttons
		for(Button b : buttons) b.update();
		// draw canvas and actual image
		image(canvas,width/2-100,height/2);
		image(actualImage, 600, 210);
		fill(255);
		textSize(12);
		text(results,600,350);
	}
		
	public class Button {
		public String label;
		public int buttonX, buttonY, buttonW, buttonH;
		public boolean mouseOverButton;
		public Button(String label, int buttonX, int buttonY) 
		{
			this.label = label;
			this.buttonX = buttonX;
			this.buttonY = buttonY;
			buttonW = (int)textWidth(label)+16;
			buttonH = 32;
			mouseOverButton = false;
		}
		public void update()
		{
			mouseOverButton = Math.abs(mouseX-buttonX) < buttonW/2 && Math.abs(mouseY-buttonY) < buttonH/2;
			if(mouseOverButton) { fill(160); stroke(0); }
			else { fill(0); stroke(255); }			
			rect(buttonX,buttonY,buttonW,buttonH,8);
			if(mouseOverButton) { fill(0); stroke(255); }
			else { fill(255); stroke(0); }			
			text(label,buttonX,buttonY);		
		}
	}

	public static final int threshold = 64;
	public PImage cropAndResize(PImage image)
	{
		// copy image data in int[][] data
		image.resize(28, 28);
		image.loadPixels();
		int[][] data = new int[image.height][image.width];
		for(int j=0;j<image.height;j++)
			for(int i=0;i<image.width;i++)
				data[j][i] = image.pixels[j*data[0].length+i];		
		
		// find edges
		int top = 0;
		for(int j=0;j<data.length;j++)
			for(int i=0;i<data[0].length;i++)
				if(brightness(data[j][i]) > threshold)
				{
					top = j;
					i = data[0].length;
					j = data.length;						
				}
		int bottom = data.length-1;
		for(int j=data.length-1;j>=0;j--)
			for(int i=0;i<data[0].length;i++)
				if(brightness(data[j][i]) > threshold)
				{
					bottom = j;
					i = data[0].length;
					j = -1;						
				}
		int left = 0;
		for(int i=0;i<data[0].length;i++)
			for(int j=0;j<data.length;j++)
				if(brightness(data[j][i]) > threshold)
				{
					left = i;
					i = data[0].length;
					j = data.length;						
				}
		int right = data[0].length-1;
		for(int i=data[0].length-1;i>=0;i--)
			for(int j=0;j<data.length;j++)
				if(brightness(data[j][i]) > threshold)
				{
					right = i;
					i = -1;
					j = data.length;						
				}

		// preserve aspect ratio
		int size = Math.max(right-left,bottom-top);
		if(right-left < size)
		{
			left -= (size - right + left)/2;
			right = left + size;
		}
		if(bottom-top < size)
		{
			top -= (size - bottom + top)/2;
			bottom = top + size;
		}

	    // crop to new data
		int[][] newData = new int[size][size];
		for(int j=0;j<size;j++)
			for(int i=0;i<size;i++)
				if(j+top < 0 || j+top >= data.length || i+left < 0 || i+left >= data[0].length)
					newData[j][i] = color(0,0,0);
				else
					newData[j][i] = data[j+top][i+left];

		// return new 20x20 image containing newData
		image = createImage(newData.length,newData[0].length,RGB);
		image.loadPixels();
		for(int j=0;j<image.height;j++)
			for(int i=0;i<image.width;i++)
				image.pixels[j*newData[0].length+i] = newData[j][i];
		image.updatePixels();
		image.resize(20, 20);
		return image;
	}
	
	public static void main(String[] args)
	{
		PApplet.main("Main");
	}
}
