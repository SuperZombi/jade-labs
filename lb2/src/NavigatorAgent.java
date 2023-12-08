import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.Map;
import java.util.Random;

public class NavigatorAgent extends Agent {
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("wumpus-navigator");
        sd.setName("Super Navigator");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException ignored) {}

        addBehaviour(new OfferRequestsServer());
        System.out.println("Wumpus Navigator: " + getAID().getName());
    }
    private static class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    System.out.println(msg.getContent());
                    Map[] choices = {
                            Map.of("action","turn", "value", "r"),
                            Map.of("action","turn", "value", "l"),
                            Map.of("action","turn", "value", "u"),
                            Map.of("action","turn", "value", "d"),
                            Map.of("action","move", "value", "true"),
                            // Map.of("action","shoot", "value", "true")
                    };
                    int randomIndex = new Random().nextInt(choices.length);
                    String randomChoice = HashMapParser.convertHashMapToString(choices[randomIndex]);
                    reply.setPerformative(ACLMessage.CFP);
                    reply.setContent(randomChoice);
                }
                myAgent.send(reply);
            }
        }
    }
}
