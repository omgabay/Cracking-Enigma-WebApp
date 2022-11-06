package bruteforce;

import auxiliary.Dictionary;
import auxiliary.Message;
import machine.Enigma;
import machine.IEngine;
import machine.parts.Reflector;
import machine.parts.Rotor;
import machine.parts.Rotors;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AgentWorker implements Runnable {
    private final Enigma machine;
    private final List<Rotor> rotors;
    private final List<Reflector> reflectors;
    private final BlockingQueue<AgentTask> tasksQueue;
    private final Dictionary dictionary;
    private final Consumer<List<AgentSolutionEntry>> solutionsConsumer;
    private boolean wasCancelled;

    // Agent Data
    private final String agentName;
    private final String teamName;



    public AgentWorker(IEngine engine, String agentName, String teamName, BlockingQueue<AgentTask> tasks, Consumer<List<AgentSolutionEntry>> sendSolutions){
        this.machine = engine.getMachine();
        this.rotors = engine.getRotorsFromEngine();
        this.reflectors = engine.getReflectorsFromEngine();
        this.dictionary = engine.getDictionary();
        this.solutionsConsumer = sendSolutions;
        this.tasksQueue = tasks;
        this.wasCancelled = false;
        this.agentName = agentName;
        this.teamName = teamName;
    }

    @Override
    public void run() {
        while(!this.wasCancelled){
            // trying to Poll a new job for 3 seconds
            AgentTask task = null;
            try{
                task =tasksQueue.poll(5000, TimeUnit.MILLISECONDS);
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            // Checking if polling from the queue was successful
            if(task == null){
                continue;
            }

            updateMachineSetup(task);
            // Getting ticks -- how many times to iterate on rotor combinations
            long ticks = task.getTicks();

            List<AgentSolutionEntry> solutions = new ArrayList<>();
            Iterator<List<Integer>> rotorsIterator = this.machine.getRotorsIterator();
            machine.setRotorPositions(task.getPositions());
            StringBuilder sb = new StringBuilder();
            sb.append(Thread.currentThread().getName()).append(" received task number ").append(task.getJobSequenceNumber());
            sb.append(" word to decrypt ").append(task.getSecretMessage());
            sb.append("\nconfiguration:").append(task.getTicks()).append(" iterations ids:").append(task.getRotorsIds()).append(" pos:").append(task.getPositions());
            sb.append("\nreflector=").append(task.getReflector()).append("\n");
            System.out.println(sb);

            for(int i=0; i<ticks; i++){
                 // Saving rotor initial position
                List<Integer> savePositions = this.machine.getPositionsIndices();
                Message message = this.machine.processText(task.getSecretMessage());

                // success is True when the processed text is a valid sentence
                String candidate = message.getProcessed().toLowerCase();
                System.out.println("candidate = " + candidate);



                if(dictionary.checkCorrectness(candidate)){
                    AgentSolutionEntry solutionEntry = new AgentSolutionEntry(this.agentName,this.teamName,candidate, machine.getCurrentConfiguration());
                    System.out.println(solutionEntry);
                    solutions.add(solutionEntry);
                }

                machine.setRotorPositions(savePositions);
                System.out.println(i+":" + machine.getCurrentConfiguration());
                rotorsIterator.next();

            }
            // Add all solution to the queue, later will be sent to the server

            this.solutionsConsumer.accept(solutions);
        }
    }


    private Rotor getRotorByIndex(int id) {
        for (Rotor rotor : this.rotors) {
            if(rotor.getID() == id){
                return rotor;
            }
        }
        return null;
    }

    private void updateMachineSetup(AgentTask task){
        // Getting the reflector from the task
        int reflectorId = task.getReflector();
        if(reflectorId >= 0 && reflectorId < reflectors.size()){
            Reflector rafi = this.reflectors.get(task.getReflector());
            this.machine.swapReflector(rafi);
        }


        // Getting Rotors from the new task
        List<Integer>  jobRotorsIds = task.getRotorsIds();
        LinkedList<Rotor>  newRotorList = new LinkedList<>();
        try {
            for (int id : jobRotorsIds) {
                newRotorList.addLast(this.getRotorByIndex(id));
            }

            this.machine.setRotors(new Rotors(newRotorList));

        }catch(Exception e){
            System.err.println("Rotor was not found by Agent!!");
        }
    }


    public void setCancelled(boolean flag){
        this.wasCancelled = flag;
    }

}
