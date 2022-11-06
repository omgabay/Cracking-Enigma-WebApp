package bruteforce;

import auxiliary.MachineInfo;
import machine.IEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class Decryption {

    private final LinkedList<Incrementor> incrementorList;

    private final int reflectorCount;
    private final int totalRotorCount;
    private final int alphabetSize;

    private final List<Integer> machineRotors;

    private boolean bruteforceIsActive;
    private final String secretMessage;

    private int reflectorID;





    public Decryption(IEngine engine, String secretMsg, Difficulty difficulty, long taskSize){
        this.reflectorCount = engine.getNumOfReflectors();
        this.reflectorID = engine.getMachine().getReflector().getReflectorID()-1;
        this.totalRotorCount = engine.getTotalAvailableRotors();
        this.alphabetSize = engine.getMachineAlphabet().size();
        this.machineRotors = engine.getMachine().getRotorIDs();
        this.incrementorList = new LinkedList<>();
        this.secretMessage = secretMsg;
        createIncrementors(difficulty);
        Incrementor.setJobSize(taskSize);
        bruteforceIsActive = true;
    }


    public synchronized List<AgentTask> fetchAgentTasks(int numOfTasks){
        List<AgentTask> agentTasks = new ArrayList<>();
        while(agentTasks.size() < numOfTasks && !incrementorList.isEmpty()){
            Incrementor incrementor = incrementorList.removeFirst();
            System.out.println("inc:"+incrementor.getValue()+"/"+incrementor.getMaxValue());
            agentTasks.addAll(incrementor.getNewJobs());
            if(!incrementor.finished()){
                incrementorList.addLast(incrementor);
            }
        }

        return agentTasks;
    }



    private void createIncrementors(Difficulty difficulty) {
        switch(difficulty){
            case EASY: default:
                createIncrementorsLevelEasy();
                break;
            case MEDIUM:
                createIncrementorsLevelMedium();
                break;
            case HARD:
                createIncrementorsLevelHard();
                break;
            case INSANE:
                createIncrementorsLevelCrazy();
                break;
        }
    }


    private void createIncrementorsLevelEasy() {
        this.incrementorList.add(new Incrementor(machineRotors,alphabetSize,this.reflectorID ,this.secretMessage));
    }




    private void createIncrementorsLevelMedium() {



        this.incrementorList.add(new AdvancedIncrementor(machineRotors,alphabetSize,reflectorCount, this.secretMessage));
    }

    private void createIncrementorsLevelHard(){

        List<List<Integer>> permutations = getAllPermutations(machineRotors);

        System.out.println("permutation: " + permutations);

        System.out.println("Number of permutation is " + permutations.size());

        for (List<Integer> rotorPermutation   : permutations) {
            this.incrementorList.add(new AdvancedIncrementor(rotorPermutation,alphabetSize,reflectorCount, this.secretMessage));
        }
    }


    private void createIncrementorsLevelCrazy() {
        int selectedRotors = this.machineRotors.size();
        int totalRotors = this.totalRotorCount;

        for (List<Integer>  rotors  : subsets(totalRotors , selectedRotors)){
            List<List<Integer>> permutations = getAllPermutations(rotors);
            for (List<Integer> rotorPermutation   : permutations) {
                this.incrementorList.add(new AdvancedIncrementor(rotorPermutation,alphabetSize,reflectorCount,secretMessage));
            }
            System.out.println(permutations);
        }
    }


    private static List<List<Integer>> getAllPermutations(List<Integer> list) {

        if (list.size() == 0) {
            List<List<Integer>> result = new ArrayList<List<Integer>>();
            result.add(new ArrayList<Integer>());
            return result;
        }

        List<List<Integer>> returnMe = new ArrayList<List<Integer>>();

        Integer firstElement = list.remove(0);

        List<List<Integer>> recursiveReturn = getAllPermutations(list);
        for (List<Integer> li : recursiveReturn) {

            for (int index = 0; index <= li.size(); index++) {
                List<Integer> temp = new ArrayList<Integer>(li);
                temp.add(index, firstElement);
                returnMe.add(temp);
            }

        }
        return returnMe;
    }
    private List<List<Integer>> subsets(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(n, k, 1, result, new ArrayList<>());
        return result;
    }

    private void backtrack(int n, int k, int startIndex, List<List<Integer>> result, List<Integer> partialList) {
        if (k == partialList.size()) {
            result.add(new ArrayList<>(partialList));
            return;
        }
        for (int i = startIndex; i <= n; i++) {
            partialList.add(i);
            backtrack(n, k, i + 1, result, partialList);
            partialList.remove(partialList.size() - 1);
        }
    }



    public boolean isDone() {
        return this.incrementorList.isEmpty() || !bruteforceIsActive;
    }


    public void stopBruteforceTasks(){
        bruteforceIsActive = false;
    }

    public void continueBruteforceTask(){
        bruteforceIsActive = true;
    }
}
