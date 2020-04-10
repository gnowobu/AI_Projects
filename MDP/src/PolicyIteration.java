import java.util.Random;
import java.util.Vector;
import Jama.*;

public class PolicyIteration {
    private MDP mdp;
    public int count;

    public PolicyIteration(MDP mdp) {
        this.mdp = mdp;
    }

    public void setRandompolicy() {
        for (int i = 0; i < mdp.states.length; i++)
            for (int j = 0; j < mdp.states[0].length; j++) {
                if(mdp.states[i][j] ==  null) continue;

                Random r = new Random();
                mdp.states[i][j].action = r.nextInt(4);//set random action
            }
    }

    public void policyEvaluation(){//evaluate the policy by solving the linear equation. Here size = grid.size*grid.size
        int currentState;
        int size = mdp.numReachableStates;
        double[][] A = new double[size][size];
        double[][] b = new double[size][size];

        for (int i = 0; i < size; ++i){
            b[i][0] = 0.;
            for (int j = 0; j < size; ++j)
                A[i][j] = 0.;
        }
        currentState = 0;
        State s = (State)mdp.reachableStates.get(currentState);

        while(currentState < size){
            int sIndex = s.index;

            A[sIndex][sIndex] = 1.0;
            b[sIndex][0] = s.reward;

            Vector T = mdp.getTransition(s, s.action);
            int k = T.size();

            for(int k1 = 0; k1 < k; k1++){

                Transition t = (Transition) T.get(k1);
                double prob = t.probability;
                State sPrime = t.nextState;
                if (sPrime.terminal) {
                    b[sIndex][0] += mdp.gamma * prob * sPrime.current_utility;
                } else
                    A[sIndex][sPrime.index] -= mdp.gamma * prob;
            }

            currentState++;
            if(currentState >= size) break;
            else
                s = (State)mdp.reachableStates.get(currentState);
        }

        Matrix mA = new Matrix(A);
        if(mA.cond() > 1e3)
            throw (new ArithmeticException("Singular solution matrix."));

        Matrix mb = new Matrix(b);
        Matrix x = mA.solve(mb);

        for (int i = 0; i < size; ++i)
            mdp.setUtility(i, x.get(i, 0));

    }


    public void Solve() {
        boolean unchanged;
        State s;
        count = 0;


        setRandompolicy();
        do {
            unchanged = true;
            policyEvaluation();

            for (int i = 0; i < mdp.states.length; i++) {
                for (int j = 0; j < mdp.states[0].length; j++) {
                    s = mdp.states[i][j];
                    if (s == null || s.terminal) {
                        continue;
                    }

                    int policyAction = s.action;

                    double max_Util = -Double.MAX_VALUE;
                    double policy_Util = 0;
                    int max_Action = -1;

                    for (int k = 0; k < mdp.numPossibleaction; k++) {
                        Vector transitions = mdp.getTransition(s, k);//possible transition from s with action k
                        int size = transitions.size();
                        double nextUtil = 0;

                        for (int t = 0; t < size; t++) {
                            Transition T = (Transition) transitions.get(t);
                            double prob = T.probability;
                            State sPrime = T.nextState;
                            nextUtil += (prob * sPrime.current_utility);
                        }
                        if (k == policyAction)
                            policy_Util = nextUtil;

                        if (nextUtil > max_Util) {
                            max_Util = nextUtil;
                            max_Action = k;
                        }
                    }

                    if (max_Util > policy_Util) {
                        unchanged = false;
                        s.action = max_Action;

                    }
                }
            }
            count++;

        } while(!unchanged);
    }
 public static void main(String[] args) {
        /** In case the process of read the input file fails, you can use this main function
         * to test the value iteration class.
         *
         */
        double[][] grid = {{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0, -1.0, 0.0, 1.0, 0.0},
                {-1.0, 0.0, 0.0, -1.0, 0.0, 4.0, 0.0},
                {0.0, 1.0, 0.0, -1.0, 0.0, 1.0, 0.0},
                {0.0, 100.0, 0.0, -100.0, 0.0, 3.0, 0.0},
                {0.0, 2.0, 0.0, -1.0, 0.0, 3.0, 0.0},
                {0.0, 0.0, 0.0, -1.0, 0.0, 1.0, 0.0},
        };
        double[] noise = {0.8, 0.1, 0, 0.1};
        double gamma = 0.9;
        MDP mdp = new MDP(gamma, noise, grid, grid.length);
        PolicyIteration test = new PolicyIteration(mdp);
        // mdp.setWall(1,1);
        test.Solve();
        mdp.printValue();
        mdp.printSign();
        System.out.println("number of iteration: " + test.count);
    }
}
