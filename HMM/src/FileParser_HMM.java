import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileParser_HMM { // only for given type of input file

    public Scanner scanner;
    public File file;
    public String path;

    public double[][] Emission = new double[3][3];
    public double[][] Transition;
    public double p;

    public FileParser_HMM(String path){
        this.path = path;

    }

    /**
     * read the txt file, just store them as string[], removing all the notations (#...)
     * @return all the content in the file
     */
    public String[] read(){

        try {
            file = new File(path);
            scanner = new Scanner(file);
            int ctr = 0;

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(line.charAt(0) == '#') continue;
                ctr++;
                //scanner.nextLine();
            }

            String[] input = new String[ctr];

            scanner = new Scanner(file);
            String line = scanner.nextLine();

            ctr = 0;
            while(scanner.hasNextLine()){
                if(line.charAt(0) == '#') line = scanner.nextLine();
                else{
                    input[ctr] = line;
                    ctr++;
                    line = scanner.nextLine();
                }
            }

            return input;

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;

    }

    public void initParameterHMM_DICE(String[] input){


        String[][] temp = new String[3][3];

        p = Double.parseDouble(input[0]); //probability of changing to a new dice each turn.

        Transition = new double[][]{
                {p,((1-p)/2),((1-p)/2)},
                {((1-p)/2),p,((1-p)/2)},
                {((1-p)/2),((1-p)/2),p}};

        for(int i = 1; i <= 3; i++){
             temp[i-1] = input[i].split(" "); //Emission probabilities for dice problem, from 2nd line to 4th line
        }

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++) Emission[i][j] = Double.parseDouble(temp[i][j]);

    }

    public int[] getObservation(String[] input){

        String[] temp1 = input[4].split(",");
        int[] observation = new int[temp1.length];

        for(int i = 0; i < temp1.length; i++)
            observation[i] = Integer.parseInt(temp1[i]);

        return observation;
    }

    public HiddenMarkovModel createHMM_DICE(){
        initParameterHMM_DICE(read());
        HiddenMarkovModel hmm = new HiddenMarkovModel(Emission, Transition);

        hmm.observation = getObservation(read());
        return hmm;
    }



    /*public static void main(String[] args) {
        String path = "./test.txt";
        FileParser_HMM test = new FileParser_HMM(path);

        HiddenMarkovModel hmm = test.createHMM_DICE();
        int[] observation = hmm.observation;
        double[] init = new double[]{0.333, 0.333, 0.333};
        hmm.setC(init);

        HMMSolver solver = new HMMSolver();

        int[] states;

        states = solver.viterbiAlg(hmm, observation);
        solver.printSequence(states);

    }*/

}
