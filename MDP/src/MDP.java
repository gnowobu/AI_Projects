
import java.util.Vector;

public class MDP {

    public double gamma;
    public double[] noise;
    public double[][] grid;
    public State[][] states;
    public double[][] dir_noise;
    public int size;

    //public double[][] utility;
    //public double reward;

    Vector transitions;
    int numPossibleaction = 4;//Four directions, stay is NOT an action here
    int numResultingStates = numPossibleaction + 1;//consider the resulting action of stay
    double[][][] transitionModel;
    Vector reachableStates;
    int numReachableStates = 0;
    int currentState;

    /**abs2relative
     * translate (r',c') into a direction code based on (r,c).
     * the code is the same as action code, which means code 0 represents (r',c') is on the upside of (r,c)...
     * dr = r'-r, dc = c' -c
     * <pre>
     * dr  dc  |  dr+1  dc+1  |  direction  | code
     * -1  -1  |     0     0  |     x       | -1
     * -1   0  |     0     1  |    UP       |  0
     * -1  +1  |     0     2  |     x       | -1
     *  0  -1  |     1     0  |   LEFT      |  3
     *  0   0  |     1     1  |   STAY      |  4
     *  0  +1  |     1     2  |   RIGHT     |  1
     * +1  -1  |     2     0  |     x       | -1
     * +1   0  |     2     1  |   DOWN      |  2
     * +1  +1  |     2     2  |     x       | -1
     * </pre>
     */
    int[][] abs2relative = {{-1,0,-1},{3,4,1},{-1,2,1}};

    public MDP(double gamma, double[] noise, double[][] grid, int size) {

        this.size = size;
        this.gamma = gamma;
        this.noise = noise;
        this.grid = grid;

        states = new State[grid.length][grid[0].length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != 0) {
                    if(grid[i][j] == -10000){
                        setWall(i, j);
                    }
                    else {
                        State s = new State(i, j, grid[i][j], true);
                        s.reward = s.current_utility;
                        states[i][j] = s;
                    }
                    //terminal[i][j] = true; //terminal state here
                } else {
                    states[i][j] = new State(i, j, grid[i][j], false);//default reward is 0;
                }
            }
        }
        dir_noise = new double[][]{
                {noise[0],noise[1],noise[2],noise[3]},//the main direction is up
                {noise[3],noise[0],noise[1],noise[2]},//the main direction is right
                {noise[2],noise[3],noise[0],noise[1]},//down
                {noise[1],noise[2],noise[3],noise[0]}};

        compileStates();

        setTransition();

    }



    /**
     * The function is actually part of construction of this object.  Better
     * to put it explicitly in the constructor, instead of wishing the user
     * to call this voluntarily.
     */
    public void compileStates() {
        State s;
        int index = 0;

        reachableStates = new Vector(size * size);

        for(int i=0; i<states.length; ++i)
            for(int j=0; j<states[0].length; ++j) {


                s = states[i][j];

                // a wall
                if(s == null)
                    continue;

                s.index = index;
                index ++;
                reachableStates.add(s);//can find a state with index
            }

        // Allocate space for the transition model.
        // The size of the array is based on the assumption that
        // only numActions of states (s') can be reached from state s,
        // although at first glance, the size is index*numActions*index.
        // The size of last dimension is numActions+1 is because we
        // need to consider stay too.
        transitionModel = new double[index][numPossibleaction][numResultingStates];

        // The following is unnecessary.  But that Java does it for us
        // doesn't mean we shouldn't do it, for the correctness of this
        // model
        for(int i = 0; i < index; ++i)
            for(int j = 0; j < numPossibleaction; ++j)
                for(int k = 0; k < numResultingStates; ++k)
                    transitionModel[i][j][k] = 0.;

        // A list of probability-state pairs.
        transitions = new Vector();

        this.numReachableStates = index;
    }



    /**
     * This function is for setting up the transition model.
     * @param r Row number (starts from 1) of current state.
     * @param c Column number (starts from 1) of current state.
     * @param action Action to be taken.
     * @param rp Row number of next state, after the action is performed.
     * @param cp Column number of next state.
     * @param prob The probability of this chain of action: T(s,a,s').
     */
    public void setTransitionProbability(int r, int c, int action, int rp, int cp, double prob) {

        State s = states[r-1][c-1];
        if(s.terminal)
            return;

        int nextStateIndex = abs2relative[rp-r+1][cp-c+1];//relative index for the possible states
        transitionModel[s.index][action][nextStateIndex] += prob;//the relative index is the same as actions.
                                                                // e.g nextStateIndex = 0 means the destination is north.
         }

    /**
     * Set T(s,a,s').
     * @param s The source state.
     * @param action The action to be taken.
     * @param sp The destination state.
     * @param p The probability.
     */
    public void setTransitionProbability(State s, int action, State sp, double p) {

        setTransitionProbability(s.i+1, s.j+1, action, sp.i+1, sp.j+1, p);
    }



    public void setTransition(){


        for(int i = 0; i < states.length; i++){
            for(int j = 0; j < states[0].length; j++){
                State s = states[i][j];
                if(s == null) continue;

                for(int k = 0; k < numPossibleaction; k++){
                    for(int temp = 0; temp < numResultingStates - 1; temp++){
                        State stemp = move(s,temp);
                        setTransitionProbability(s,k,stemp,dir_noise[k][temp]);
                    }

                }
            }
        }
    }

    /**
     * Used (solely) by policy evaluation.  Setting the utility value of a state.
     * @param index The index of the state.
     * @param u The utility value.
     */
    public void setUtility(int index, double u) {
        ((State)reachableStates.get(index)).current_utility = u;
    }



    /**
     * This is the transition function, T(s,a,s') in the textbook.
     *
     * @param s The current state.
     * @param action Action to be taken.
     * @return A list of (probability,next-state) pairs.
     */
    public Vector getTransition(State s, int action) {
        Vector transitions = new Vector();

        // If s is a terminate state, no transition function for it.
        // Return an empty vector.
        if(s.terminal)
            return transitions;

        double p;
        State nextState;
        for(int i = 0; i < numResultingStates; ++i) {

            p = transitionModel[s.index][action][i];
            nextState = move(s, i);

            transitions.add(new Transition(p, nextState));

            }

        return transitions;
    }

    /**
     * Move on the environment.  It'll never return a null (a unreachable
     * state).
     *
     * @param s Current state.
     * @param action Type of movement.
     * @return Next state after the movement.
     */
    public State move(State s, int action){
        int row = s.i;
        int col = s.j;
        int rows = states.length;
        int cols = states[0].length;

        if (action == 4) //action_stay = 4
            return s;

        // If the movement is not possible, it stays where it was.
        // This is a hidden rule and should be revealed to the outside
        // world through some interface(s).
        // If there's a hole, it'll return null.  All taken care of
        // by grid[][].
        switch (action) {
            case 0://up
                row -= 1;
                if(row < 0)
                    row = 0;
                break;

            case 1://right
                col += 1;
                if(col > cols-1)
                    col = cols-1;
                break;

            case 2://down
                row += 1;
                if(row > rows-1)
                    row = rows-1;
                break;

            case 3://left
                col -= 1;
                if(col < 0)
                    col = 0;
                break;
        }
        // if the destination is a hole, stay where it was.
        if(states[row][col] == null)
            return s;
        else
            return states[row][col];
    }


    public void setWall(int row, int col){
        // set the block to wall
        states[row][col] = null;
    }

    public void printValue(){
        for(int i = 0; i < states.length; i++){
            for(int j = 0; j < states[0].length; j++){
                if(states[i][j] == null)
                    System.out.print('W' + " ");
                else{
                    String strDouble = String.format("%7.2f", states[i][j].current_utility);
                    System.out.print(strDouble + " ");
                }
            }
            System.out.print("\n");
        }

    }



    public String toSign(int action) {
        switch (action) {
            case 0: return "^";
            case 1: return ">";
            case 2: return "V";
            case 3: return "<";
        }
        return null;
    }

    public void printSign(){
        for(int i = 0; i < states.length; i++){
            for(int j = 0; j < states[0].length; j++){
                if(states[i][j] == null) System.out.print("W ");
                else if(states[i][j].terminal)
                    System.out.print("T ");

                else
                    System.out.print(toSign(states[i][j].action) + " ");
            }
            System.out.println("\n");
        }
    }


}

