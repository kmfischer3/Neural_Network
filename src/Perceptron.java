import java.util.ArrayList;
import java.util.Random;

public class Perceptron 
{	
	public static final double LEARNING_RATE = 0.1;
	public static final Random randGen = new Random();
	
	public int index; // current perceptron's index with it's layer
	public ArrayList<Perceptron> outputs; // next layer of perceptrons
	public ArrayList<Perceptron> inputs; // previous layer of perceptrons
	 
	public double activationValue;
	public double delta;
	public double biasInputWeight;
	public static double biasAV = 1.0;
	public ArrayList<Double> inputWeights;
	public ArrayList<Double> outputWeights;
		
	public Perceptron(int index, ArrayList<Perceptron> inputs)
	{
		this.index = index;
		this.inputs = inputs;
		outputs = new ArrayList<Perceptron>();
		
		this.activationValue = 2.0*(randGen.nextDouble())-1;
		this.delta = 0.0;
		this.biasInputWeight = 2.0*(randGen.nextDouble())-1;

		this.inputWeights = new ArrayList<Double>();
		this.outputWeights = new ArrayList<Double>();
	}

	public void activate()
	{
		// calculate a new activation value
		
		// for perceptrons in input layer, do nothing
		if (this.inputs != null) {
			double sum = 0.0;
			for (int i = 0; i < this.inputs.size(); i++) {
				sum += ( this.inputs.get(i).activationValue * this.inputWeights.get(i) );
			}
			
			sum += biasAV*this.biasInputWeight;
			
			this.activationValue = logisticalFunction(sum);

		}
	}
	
	public void calculateDeltas(double[] expectedOutputs)
	{
		// calculate deltas for perceptrons in the output layer
		this.delta = -2.0*(expectedOutputs[this.index] - this.activationValue)*this.activationValue*(1.0-this.activationValue);
	}
	
	public void calculateDeltas()
	{
		// calculate deltas for non-output layer perceptrons
		double sum = 0.0;
		for (int i = 0; i < this.outputs.size(); i++) {
			sum += ( this.outputWeights.get(i)*this.outputs.get(i).delta );
		}
		
		sum+= biasAV*this.biasInputWeight;
		this.delta = sum*this.activationValue*(1.0-this.activationValue);
		
	}
	
	public void updateWeights()
	{
		// use deltas to update weights 
		// for all non-input layer perceptrons
		if (this.inputs != null) {
			
			for (int i = 0; i < this.inputWeights.size(); i++) {
				double updated = inputWeights.get(i) - (LEARNING_RATE*this.delta*this.inputs.get(i).activationValue);
				inputWeights.set(i, updated);				
			}
			this.biasInputWeight = this.biasInputWeight - (LEARNING_RATE*this.delta*biasAV);
		}
	}
	
	private static double logisticalFunction(double x)
	{
		x = (-1.0)*x;
		return 1.0/(1.0 + Math.pow(Math.E, x));
	}
	
	
	public void print(String str) {
		
	//---------------------input info---------------------
		System.out.println(str + "-level [" + this.index + "] perceptron info:");
		System.out.println("activationValue: " + this.activationValue);
		try{
			System.out.println("inputs: ");
			for (Perceptron P : this.inputs){
				System.out.print(P.index + " ");
			}
			System.out.println();
		} catch (NullPointerException e) {
			System.out.println("[inputs array was null]");
		}
		try{
			System.out.println("input weights: ");
			for (Double D : this.inputWeights){
				System.out.print(D + " ");
			}
			System.out.println();
		} catch (NullPointerException e) {
			System.out.println("[inputWeights array was null]");
		}	
		
	//---------------------output info---------------------
		try{
			System.out.println("outputs: ");
			for (Perceptron P : this.outputs){
				System.out.print(P.index + " ");
			}
			System.out.println();
		} catch (NullPointerException e) {
			System.out.println("[outputs array was null]");
		}
		try{
			System.out.println("output weights: ");
			for (Double D : this.outputWeights){
				System.out.print(D + " ");
			}
			System.out.println();
		} catch (NullPointerException e) {
			System.out.println("[outputWeights array was null]");
		}	
		System.out.println();
		System.out.println();

	}
}
