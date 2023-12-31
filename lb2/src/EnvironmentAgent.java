import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.*;

public class EnvironmentAgent extends Agent {
    private GameElement[][] Board;
    private int rows;
    private int columns;
    String shootResult = "";

    protected void setup() {
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
        System.out.println("Wumpus Environment: " + getAID().getName());

        rows = 4; columns = 4;
        Board = new GameElement[rows][columns];
        Board[1][0] = new GameElement("Wumpus");
        Board[1][1] = new GameElement("Gold");
        Board[0][3] = new GameElement("Pit");
        Board[1][2] = new GameElement("Pit");
        Board[3][2] = new GameElement("Pit");
        Board[3][0] = new Hero("r");
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
        System.out.println("+---------------+\n");
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
                            if (i >= 0 && i < rows && j >= 0 && j < columns && (i == heroRow || j == heroColumn)) {
                                GameElement nearbyElement = Board[i][j];
                                if (nearbyElement == null){continue;}
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
                    if (!Objects.equals(shootResult, "")){
                        if (Objects.equals(shootResult, "scream")){
                            nearbyObjects[4] = "scream";
                        } else if (Objects.equals(shootResult, "bump")){
                            nearbyObjects[3] = "bump";
                        }
                        shootResult = "";
                    }
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(Arrays.toString(nearbyObjects));
                    displayBoard();
                }
                else if (reply.getPerformative() == ACLMessage.CFP) {
                    HashMap<String, String> hashMap = HashMapParser.parseStringToHashMap(msg.getContent());
                    System.out.println(hashMap);
                    String action = hashMap.get("action");
                    int[] heroPos = getGameObjects("Hero").get(0);
                    Hero hero = (Hero) Board[heroPos[0]][heroPos[1]];
                    if (Objects.equals(action, "turn")){
                        hero.view_direction = hashMap.get("value");
                        reply.setPerformative(ACLMessage.AGREE);
                        reply.setContent("OK");
                    }
                    else if (Objects.equals(action, "move")){
                        int[] futureHeroPos = hero.moveFoward(heroPos[0], heroPos[1]);
                        if (futureHeroPos[0] < rows    && futureHeroPos[0] >= 0 &&
                            futureHeroPos[1] < columns && futureHeroPos[1] >= 0){
                            if (Board[futureHeroPos[0]][futureHeroPos[1]] == null){
                                Board[futureHeroPos[0]][futureHeroPos[1]] = hero;
                                Board[heroPos[0]][heroPos[1]] = null;
                                reply.setPerformative(ACLMessage.AGREE);
                                reply.setContent("OK");
                            }
                            else if (Board[futureHeroPos[0]][futureHeroPos[1]].name == "Gold"){
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                reply.setContent("You win");
                            }
                            else{
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                reply.setContent("You died");
                            }
                        } else{
                            reply.setPerformative(ACLMessage.CANCEL);
                            reply.setContent("NO");
                        }
                    }
                    else if (Objects.equals(action, "shoot")){
                        shootResult = "bump";
                        List<int[]> wumpusRequest = getGameObjects("Wumpus");
                        if (!wumpusRequest.isEmpty()) {
                            int[] wumpusPos = wumpusRequest.get(0);
                            if (Objects.equals(hero.view_direction, "r")) {
                                if (wumpusPos[0] == heroPos[0] && wumpusPos[1] > heroPos[1]) {
                                    shootResult = "scream";
                                }
                            } else if (Objects.equals(hero.view_direction, "l")) {
                                if (wumpusPos[0] == heroPos[0] && wumpusPos[1] < heroPos[1]) {
                                    shootResult = "scream";
                                }
                            } else if (Objects.equals(hero.view_direction, "u")) {
                                if (wumpusPos[1] == heroPos[1] && wumpusPos[0] < heroPos[0]) {
                                    shootResult = "scream";
                                }
                            } else if (Objects.equals(hero.view_direction, "d")) {
                                if (wumpusPos[1] == heroPos[1] && wumpusPos[0] > heroPos[0]) {
                                    shootResult = "scream";
                                }
                            }
                            if (Objects.equals(shootResult, "scream")) {
                                Board[wumpusPos[0]][wumpusPos[1]] = null;
                            }
                        }
                    }
                }
                myAgent.send(reply);
            }
        }
    }
}
