public class HMMSolver {


    /**TODO
     * forward inference, calculate the probability of an observation series given a HMM
     * @param hmm a Hidden Markov Model
     * @param observations evidence/observation sequence
     */
    public void forward(HiddenMarkovModel hmm, int[] observations){

    }

    /**
     * @param hmm a Hidden Markov Model
     * @param observations evidence/observation sequence
     * @return the most-possible sequence of given emission/observation, represented by the index of dice
     */
    public int[] viterbiAlg(HiddenMarkovModel hmm, int[] observations){
        int size_Time = observations.length;
        int size_State = hmm.C.length; //number of states. Equals to the length of initial distribution of HMM
        int[] sequence = new int[size_Time];

        double[][] sigma = new double[size_State][size_Time];
        int[][] path = new int[size_State][size_Time];

        for(int i = 0; i < size_State; i++){
            sigma[i][0] = hmm.C[i] * hmm.B[i][observations[0] - 1]; //C is initial distribution, B is emission probability. -1 to match index
            path[i][0] = 0;
        }

        for(int i = 1; i < size_Time; i++){//from T=1 (Initial time is 0)
            for(int j = 0; j < size_State; j++) {

                double max_prev = -Double.MAX_VALUE; //to record the max of A[s'][s] * sigma[s'][t-1]
                int max_state = -1;// record the previous state that maximizes the probability of this state (argmax)

                for (int k = 0; k < size_State; k++) { //time = T-1
                    double value = sigma[k][i - 1] * hmm.A[k][j]; //find max A(k,j) * sigma(t-1,k)
                    if (value > max_prev) {
                        max_prev = value;
                        max_state = k;
                    }
                }
                path[j][i] = max_state;
                sigma[j][i] = max_prev * hmm.B[j][observations[i] - 1]; // update, observation[i] - 1 to match index
            }
        }

        int path_pointer = -1;
        for(int i = 0; i < size_State; i++){
            double max_terminal = -Double.MAX_VALUE;

            if(sigma[i][size_Time - 1] > max_terminal){//the max sigma at time T
                max_terminal = sigma[i][size_Time - 1];
                path_pointer = i;//pointer of max state at time T
            }

        }
        sequence[size_Time - 1] = path_pointer;

        for(int t = size_Time - 2; t >= 0; t--){
            sequence[t] = path[sequence[t+1]][t+1];
        }


        return sequence;
    }

    /**
     * @param hmm
     * @param state
     * @return the probability of a given state series
     *
     */
    public double sequenceProb(HiddenMarkovModel hmm, int[] state, int[] observation){
        double prob = hmm.C[state[0]] * hmm.B[state[0]][observation[0] - 1];

        int i = 0;
        while(i < observation.length - 1){
            int j = i + 1;
            prob *= hmm.A[state[i]][state[j]] * hmm.B[state[j]][observation[j] - 1];
            i++;
        }


        return Math.log10(prob);//a simple normalization

    }

    public void printSequence(int[] states){

        for(int i = 0; i < 100; i++){
            int index = states[i] + 1;
            System.out.print("D" + index + " ");
            //System.out.print(states[i]);
        }
    }



}
