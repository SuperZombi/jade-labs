import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class HeroAgent extends Agent {
    private AID enviroment_agent;

    protected void setup() {
        System.out.println("Hero agent " + getAID().getName());

        addBehaviour(new OneShotBehaviour() {
            public void action(){
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("wumpus-game");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(this.getAgent(), template);
                    enviroment_agent = result[0].getName();

                    makeAction();
                }
                catch (FIPAException ignored) { }
            }
        } );
        addBehaviour(new OfferRequestsServer());
    }

    public void makeAction(){
        addBehaviour(new OneShotBehaviour() {
            public void action(){
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(enviroment_agent);
                msg.setConversationId("get-state");
                msg.setContent("Get state");
                send(msg);
            }
        } );
    }

    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    System.out.println(msg.getContent());
                }
            }
        }
    }
}
