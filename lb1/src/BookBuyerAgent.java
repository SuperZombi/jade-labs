import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class BookBuyerAgent extends Agent {
	private String targetBookTitle;
	private int targetBookPrice;
	private BookBuyerGui myGui;

	protected void setup() {
		System.out.println("Buyer-agent " + getAID().getName() + " started");

		myGui = new BookBuyerGui(this);
		myGui.showGui();

		addBehaviour(new CyclicBehaviour(this) {
			public void action(){
				ACLMessage reply = myAgent.receive();
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						int price = Integer.parseInt(reply.getContent());
						targetBookPrice = price;
						log("«" + targetBookTitle + "» price is " + price);
						myGui.displayBookPrice(price);
					}
					else if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
						log("«" + targetBookTitle + "» successfully bought");
						myGui.displaySuccessOperation(targetBookTitle, targetBookPrice);
					}
					else if (reply.getPerformative() == ACLMessage.FAILURE){
						log("«" + targetBookTitle + "» not found");
						myGui.displayNotFound(targetBookTitle);
					}
				}
			}
		} );
	}

	public void makeSearchRequest(final String bookTitle) {
		addBehaviour(new OneShotBehaviour() {
			public void action(){
				targetBookTitle = bookTitle;
				log("Trying to find «" + targetBookTitle + "»");

				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(new AID("Seller", AID.ISLOCALNAME));
				msg.setConversationId("book-selling");
				msg.setContent(targetBookTitle);
				send(msg);
			}
		} );
	}
	public void confirmBuy() {
		addBehaviour(new OneShotBehaviour() {
			public void action(){
				ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
				msg.addReceiver(new AID("Seller", AID.ISLOCALNAME));
				msg.setConversationId("book-selling");
				msg.setContent(targetBookTitle);
				send(msg);
			}
		} );
	}

	protected void log(String text) {
		System.out.println("[Buyer] " + text);
	}

	protected void takeDown() {
		// myGui.dispose();
		System.out.println("Buyer-agent " + getAID().getName() + " leave");
	}
}
