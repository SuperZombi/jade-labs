import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EnvironmentAgent extends Agent {
    private GameElement[][] Board;
    private int rows;
    private int columns;

    protected void setup() {
        System.out.println("Wumpus Environment: " + getAID().getName());

        rows = 4; columns = 4;
        Board = new GameElement[rows][columns];
        Board[1][0] = new GameElement("Wumpus");
        Board[1][1] = new GameElement("Gold");
        Board[0][3] = new GameElement("Pit");
        Board[1][2] = new GameElement("Pit");
        Board[3][2] = new GameElement("Pit");
        Board[3][0] = new Hero("r");

        displayBoard();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("wumpus-game");
        sd.setName("Treasure Cave Walk");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException ignored) {}

        addBehaviour(new OfferRequestsServer());
    }

    void displayBoard() {
        System.out.println("+---------------+");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print("|");
                if (Board[i][j] != null) {
                    if (Board[i][j].name == "Hero"){
                        System.out.print(Board[i][j]);
                    } else{
                        System.out.print(" " + Board[i][j] + " ");
                    }
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println("|");
        }
        System.out.println("+---------------+");
    }
    List<int[]> getGameObjects(String targetName){
        ArrayList<int[]> coordinatesList = new ArrayList<>();
        for (int i = 0; i < Board.length; i++) {
            for (int j = 0; j < Board[i].length; j++) {
                GameElement element = Board[i][j];
                if (element != null && element.name.equals(targetName)) {
                    coordinatesList.add(new int[]{i, j});
                }
            }
        }
        return coordinatesList;
    }

    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                if (reply.getPerformative() == ACLMessage.REQUEST) {
                    // stench, breeze, glitter, bump, scream
                    String[] nearbyObjects = new String[5];

                    int[] heroPos = getGameObjects("Hero").get(0);
                    int heroRow = heroPos[0]; int heroColumn = heroPos[1];

                    for (int i = heroRow - 1; i <= heroRow + 1; i++) {
                        for (int j = heroColumn - 1; j <= heroColumn + 1; j++) {
                            if (i >= 0 && i < Board.length && j >= 0 && j < Board[i].length && (i == heroRow || j == heroColumn)) {
                                GameElement nearbyElement = Board[i][j];
                                if (nearbyElement.name == "Wumpus") {
                                    nearbyObjects[0] = "stench";
                                } else if (nearbyElement.name == "Pit") {
                                    nearbyObjects[1] ="breeze";
                                } else if (nearbyElement.name == "Gold") {
                                    nearbyObjects[2] = "glitter";
                                }
                            }
                        }
                    }
                    System.out.println(Arrays.toString(nearbyObjects));

                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(Arrays.toString(nearbyObjects));
                }
                else if (reply.getPerformative() == ACLMessage.CFP) {
                    HashMap<String, String> hashMap = HashMapParser.parseStringToHashMap(msg.getContent());
                    String action = hashMap.get("action");
                    int[] heroPos = getGameObjects("Hero").get(0);
                    Hero hero = (Hero) Board[heroPos[0]][heroPos[1]];
                    if (action == "switch_direction"){
                        hero.view_direction = hashMap.get("value");
                        reply.setPerformative(ACLMessage.AGREE);
                        reply.setContent("OK");
                    }
                    else if (action == "move"){
                        int[] futureHeroPos = hero.moveFoward(heroPos[0], heroPos[1]);
                        if (futureHeroPos[0] > rows    || heroPos[0] < 0 ||
                            futureHeroPos[1] > columns || heroPos[1] < 0){
                            reply.setPerformative(ACLMessage.CANCEL);
                            reply.setContent("NO");
                        } else{
                            Board[futureHeroPos[0]][futureHeroPos[1]] = hero;
                            Board[heroPos[0]][heroPos[1]] = null;
                            reply.setPerformative(ACLMessage.AGREE);
                            reply.setContent("OK");
                        }
                    }
                }
                myAgent.send(reply);
            }
        }
    }
}
