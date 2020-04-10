import java.math.*;

import java.util.Vector;

public class ValueIteration {

    public double epsilon = 0.000001;
    int count;
    MDP mdp;

    public ValueIteration(MDP mdp){
        this.mdp = mdp;
    }


    public void Solve() {
        double difference;
        double gamma = mdp.gamma;
        double threshold = epsilon*(1.-gamma)/gamma;
        double difference_max;

        count = 0;

         do {//iteration starts
             difference_max = 0;
             for (int i = 0; i < mdp.grid.length; i++) {
                for (int j = 0; j < mdp.grid[0].length; j++) {

                    State s = mdp.states[i][j];

                    if (s != null && !s.terminal) {

                        double max_Util = -Double.MAX_VALUE;
                        int optimalAction = -1;

                        for (int action = 0; action < 4; action++) {

                            Vector T = mdp.getTransition(s, action); // get all possible transition of s in current action.
                            int size = T.size();
                            double nextUtil = 0;
                            for(int k = 0; k < size; ++k) {

                                Transition t = (Transition)T.get(k);
                                double prob = t.probability;
                                State sPrime = t.nextState;
                                nextUtil += (prob * sPrime.current_utility);

                            }
                            if(nextUtil > max_Util) {
                                max_Util = nextUtil;
                                optimalAction = action;
                            }

                        }

                        difference = Math.abs(s.current_utility - mdp.gamma * max_Util);

                        s.current_utility = mdp.gamma * max_Util;
                        s.action = optimalAction;

                        if(difference > difference_max)
                            difference_max = difference;

                        }


                    }
                }
             count++;
            }while(difference_max >= threshold);
        }


    public static void main(String[] args) {
        /** In case the process of read the input file fails, you can use this main function
         * to test the value iteration class.
         *
         */
        double[][] grid = {{0.0,0.0,0.0,1.0,0.0,0.0,0.0},
                {0.0,0.0,0.0,-1.0,0.0,1.0,0.0},
                {-1.0,0.0,0.0,-1.0,0.0,4.0,0.0},
                {0.0,1.0,0.0,-1.0,0.0,1.0,0.0},
                {0.0,100.0,0.0,-100.0,0.0,3.0,0.0},
                {0.0,2.0,0.0,-1.0,0.0,3.0,0.0},
                {0.0,0.0,0.0,-1.0,0.0,1.0,0.0},
        };
        double[] noise = {0.8,0.1,0,0.1};
        double gamma = 0.9;
        MDP mdp = new MDP(gamma, noise, grid, grid.length);
        ValueIteration test = new ValueIteration(mdp);
       // mdp.setWall(1,1);
        test.Solve();
        mdp.printValue();
        mdp.printSign();
        System.out.println("number of iteration: " + test.count);


    }
}
