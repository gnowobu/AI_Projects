

public class Main {

    public static void main(String[] args) {
        String path = "../test.txt";

        if(args.length != 0){
            path = args[0];
        }

        FileParser fileParser = new FileParser(path);

        int size = fileParser.size;
        double gamma = fileParser.gamma;
        double[] noise = fileParser.noise_convert;
        double[][] grid = fileParser.grid;

        MDP mdp_v = new MDP(gamma, noise, grid, size);

        ValueIteration valueIteration = new ValueIteration(mdp_v);
        valueIteration.Solve();
        System.out.println("the grid world after value iteration: ");
        mdp_v.printValue();
        System.out.print("\n");
        System.out.println("Solution created by value iteration: ");
        mdp_v.printSign();
        System.out.println("number of iteration(value iteration): " + valueIteration.count);

        System.out.print("\n");

        MDP mdp_p = new MDP(gamma, noise, grid, size);
        PolicyIteration policyIteration = new PolicyIteration(mdp_p);
        policyIteration.Solve();
        System.out.println("the grid world after policy iteration: ");
        mdp_p.printValue();
        System.out.print("\n");
        System.out.println("Solution created by policy iteration: ");
        mdp_p.printSign();
        System.out.println("number of iteration(policy iteration): " + policyIteration.count );

    }
}
