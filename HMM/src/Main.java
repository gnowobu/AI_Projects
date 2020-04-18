import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String file = "../test.txt";
        double[] init_distribution = new double[]{0.333, 0.333, 0.333}; //Initial distribution needs to be set here as input file does not specify the value

        Scanner inputreader = new Scanner(System.in);
        System.out.println("if you want to use your own test file, input the path here:. press enter to skip");

        String input = inputreader.nextLine();
        if(!input.equals(""))
            file = input;

        FileParser_HMM fileParser = new FileParser_HMM(file);

        HiddenMarkovModel hmm = fileParser.createHMM_DICE();
        hmm.setC(init_distribution); // matrix C stores the initial distribution in HMM

        hmm.modelSpecification();//print the info of this hmm

        HMMSolver solver = new HMMSolver();

        int[] states;//result of Viterbi algorithm

        states = solver.viterbiAlg(hmm, hmm.observation);

        System.out.print("\n");

        System.out.println("The most likely state sequence for given observation: ");
        solver.printSequence(states);
        System.out.print("\n");

        double prob = solver.sequenceProb(hmm, states, hmm.observation);
        System.out.println("log10 (Probability) of this state: " + prob);





    }
}
