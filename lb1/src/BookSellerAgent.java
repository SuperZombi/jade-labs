import java.util.*;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class BookSellerAgent extends Agent {
	private final Hashtable<String, Integer> catalogue = new Hashtable<>();
	private BookSellerGui myGui;

	protected void setup() {
        catalogue.put("Кобзар", 100);
		catalogue.put("Енеїда", 200);
		catalogue.put("Лісова пісня", 300);
		catalogue.put("Тигролови", 500);

		myGui = new BookSellerGui(this);
		myGui.updateTable(catalogue);
		myGui.showGui();

		addBehaviour(new OfferRequestsServer());
	}

	protected void log(String text) {
		System.out.println("[Seller] " + text);
	}

	protected void takeDown() {
		// myGui.dispose();
		System.out.println("Seller-agent " + getAID().getName() + " closed");
	}

	public void updateCatalogue(final String title, final int price) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				catalogue.put(title, price);
				System.out.println("«" + title + "» added into catalogue");
				myGui.updateTable(catalogue);
			}
		} );
	}

	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				Integer price = catalogue.get(title);
				if (price != null) {
					if (reply.getPerformative() == ACLMessage.REQUEST) {
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(String.valueOf(price.intValue()));
						log("«" + title + "» founded");
					}
					else if (reply.getPerformative() == ACLMessage.CONFIRM){
						catalogue.remove(title);
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						log(title + " sold to " + msg.getSender().getName());
						myGui.updateTable(catalogue);
					}
				}
				else {
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
		}
	}
}
