import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;

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
    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    System.out.println(msg.getContent());
                    // Turn(left), Turn(right), Forward, Shoot, Grab, or Climb
                    reply.setPerformative(ACLMessage.CFP);
                    reply.setContent("");
                }
                myAgent.send(reply);
            }
        }
    }
}
