import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.Objects;

public class HeroAgent extends Agent {
    private AID enviroment_agent;
    private AID navigator_agent;

    protected void setup() {
        System.out.println("Hero agent " + getAID().getName());

        addBehaviour(new WakerBehaviour(this, 1000) {
            public void handleElapsedTimeout(){
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("wumpus-game");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(this.getAgent(), template);
                    enviroment_agent = result[0].getName();
                }
                catch (FIPAException ignored) { }

                sd.setType("wumpus-navigator");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(this.getAgent(), template);
                    navigator_agent = result[0].getName();
                }
                catch (FIPAException ignored) { }
            }
        } );
        addBehaviour(new OfferRequestsServer());

        addBehaviour(new WakerBehaviour(this, 2000) {
            public void handleElapsedTimeout(){
                addBehaviour(new getState());
            }
        } );
        addBehaviour(new OfferRequestsServer());
    }

    private class getState extends OneShotBehaviour{
        public void action(){
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(enviroment_agent);
            msg.setConversationId("get-state");
            msg.setContent("Get state");
            send(msg);
        }
    }
    private class sendMessageToNavigaror extends OneShotBehaviour{
        private final String content;
        public sendMessageToNavigaror(String content) {
            this.content = content;
        }
        public void action(){
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(navigator_agent);
            msg.setConversationId("current-state");
            msg.setContent(this.content);
            send(msg);
        }
    }
    private class sendCommandToEnviroment extends OneShotBehaviour{
        private final String content;
        public sendCommandToEnviroment(String content) {
            this.content = content;
        }
        public void action(){
            ACLMessage msg = new ACLMessage(ACLMessage.CFP);
            msg.addReceiver(enviroment_agent);
            msg.setConversationId("command");
            msg.setContent(this.content);
            send(msg);
        }
    }

    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    String arrayString = msg.getContent();
                    System.out.println(arrayString);
                    String[] elements = arrayString.substring(1, arrayString.length() - 1).split(", ");
                    StringBuilder requestString = new StringBuilder();
                    for (String element : elements){
                        switch (element){
                            case ("stench"):{
                                requestString.append("This place is pretty stinks."); break;
                            }
                            case ("breeze"):{
                                requestString.append("I feel some breeze here."); break;
                            }
                            case ("glitter"):{
                                requestString.append("I see a bright light."); break;
                            }
                            case ("bump"):{
                                requestString.append("I don't miss, I give it a chance."); break;
                            }
                            case ("scream"):{
                                requestString.append("And so it will be with everyone."); break;
                            }
                        }
                    }
                    if (requestString.toString().isEmpty()){
                        requestString = new StringBuilder("I think, there's nothing to kill.");
                    }
                    addBehaviour(new sendMessageToNavigaror(requestString.toString()));
                }
                else if (msg.getPerformative() == ACLMessage.CFP && Objects.equals(msg.getSender(), navigator_agent)) {
                    addBehaviour(new sendCommandToEnviroment(msg.getContent()));
                }
                else if (Objects.equals(msg.getSender(), enviroment_agent)){
                    addBehaviour(new WakerBehaviour(myAgent, 3000) {
                        public void handleElapsedTimeout(){
                            addBehaviour(new getState());
                        }
                    } );
                }
            }
        }
    }
}
