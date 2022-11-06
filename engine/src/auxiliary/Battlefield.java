package auxiliary;

import jaxb.generated.CTEBattlefield;

public class Battlefield {

    enum Level{
        UNDEFINED, EASY, MEDIUM, HARD, INSANE
    }
    private final String battleName;

    private final int numberOfTeams;

    private final Level level;


    public Battlefield(CTEBattlefield battle){
        battleName = battle.getBattleName();
        numberOfTeams = battle.getAllies();
        switch(battle.getLevel()){
            case "Easy":
                level = Level.EASY;
                break;
            case "Medium":
                level = Level.MEDIUM;
                break;
            case "Hard":
                level = Level.HARD;
                break;
            case "Insane":
                level = Level.INSANE;
                break;
            default:
                level = Level.UNDEFINED;
        }
    }


    public String getBattleDifficulty(){
        return this.level.name();
    }

    public String getBattleName(){
        return this.battleName;
    }

    public int getNumberOfTeams(){
        return this.numberOfTeams;
    }



}
