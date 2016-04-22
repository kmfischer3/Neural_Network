import java.util.ArrayList;
import java.util.Random;

public class NeuralNetwork 
{
	// a 2d ArrayList of Perceptrons, where 
	// the input layer is: layers.get(0), 
	// the output layer is: layers.get(layers.size()-1)
	// and the hidden layers are in between
	public ArrayList<ArrayList<Perceptron>> layers;	
	public static final Random randGen = new Random();
	
	public NeuralNetwork()
	{		
		this.layers = new ArrayList<ArrayList<Perceptron>>();
	}
	
	public void addLayer(int size)
	{
		// make sure to set the inputs of the previous layer
		// and the outputs of the previous layer
		ArrayList<Perceptron> newLayer = new ArrayList<Perceptron>();

		if (layers.size() == 0) {			//adding input layer
			
			for (int i = 0; i < size; i++) {
				Perceptron P = new Perceptron(i, null);
				newLayer.add(P);
			}
			
		} else if (layers.size() == 1) {	//adding hidden layer
			
			for (int i = 0; i < size; i++) {
				Perceptron P = new Perceptron(i, layers.get(0));
				newLayer.add(P);
			}

		} else {							//adding output layer
			
			for (int i = 0; i < size; i++) {
				Perceptron P = new Perceptron(i, layers.get(1));
				P.outputs = null;
				newLayer.add(P);
			}
		}
		
		layers.add(newLayer);
		
		if (layers.size() == 3) connect();
		
	}
	
	public void train(double[] inputs, double[] outputs)
	{
		//learn from a single set of inputs and outputs

		//activate
		int k = 0;
		for (ArrayList<Perceptron> layer : this.layers){
			if (k == 0) {
				for (Perceptron P : layer) {
					P.activationValue = inputs[P.index];
				}
			}else{
				for (Perceptron P : layer) {
					P.activate();
				}
			}
			k++;
		}
		
		//calculateDeltas (output layer)
		for (Perceptron P : this.layers.get(2)) {
			P.calculateDeltas(outputs);
		}
		
		//calculateDeltas (input and hidden layers)
		for (Perceptron P : this.layers.get(1)) {
			P.calculateDeltas();
		}
		for (Perceptron P : this.layers.get(0)) {
			P.calculateDeltas();
		}
		
		//update Weights
		for (int c = 1; c < 3; c++){
			for (Perceptron P : this.layers.get(c)) {
				P.updateWeights();
			}
		}
		
	}
	
	public double[] predict(double[] inputs)
	{
		
		int k = 0;
		for (ArrayList<Perceptron> layer : this.layers){
			if (k == 0) {
				for (Perceptron P : layer) {
					P.activationValue = inputs[P.index];
					System.out.println("layer#" + k + ", Perceptron at index " + P.index + ":");
					System.out.println("activationValue (should be input): " + P.activationValue);
				}
			}else{
				for (Perceptron P : layer) {
					P.activate();
					System.out.println("layer#" + k + ", Perceptron at index " + P.index + ":");
					System.out.println("activationValue: " + P.activationValue);
				}
			}
			k++;
		}
		
		
		
		int i = 0;
		double[] ret = new double[10];
		
		for (Perceptron p : this.layers.get(2)){
			ret[i] = p.activationValue;
			System.out.println("Output info: " + ret[i]);
			i++;
		}
		
		return ret;
	}	
	
	public void connect() {
		
		//Connect all inputs and outputs:
		for (Perceptron P : layers.get(0)){
			P.outputs = layers.get(1);
		}
		for (Perceptron P : layers.get(1)) {
			P.inputs = layers.get(0);
			P.outputs = layers.get(2);
		}
		for (Perceptron P : layers.get(2)){
			P.inputs = layers.get(1);
		}
		
		//Assign input/output weights
		for (Perceptron P : layers.get(1)) {
			for (int i = 0; i < P.inputs.size(); i++) {
				P.inputWeights.add(2.0*P.randGen.nextDouble()-1.0);
			}
			for (int j = 0; j < P.outputs.size(); j++){
				P.outputWeights.add(2.0*P.randGen.nextDouble()-1.0);
			}
		}
		
		for (Perceptron inputPerceptron : layers.get(0)){
			for (Perceptron hiddenPerceptron : layers.get(1)){
				inputPerceptron.outputWeights.add(hiddenPerceptron.inputWeights.get(inputPerceptron.index));
			}
		}
		
		for (Perceptron outputPerceptron : layers.get(2)){
			for (Perceptron hiddenPerceptron : layers.get(1)){
				outputPerceptron.inputWeights.add(hiddenPerceptron.outputWeights.get(outputPerceptron.index));
			}
		}
	}
}
