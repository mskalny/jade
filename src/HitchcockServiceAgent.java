package jadelab1;

import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;

public class HitchcockServiceAgent extends ServiceAgent {
    protected void setup () {
        //services registration at DF
        DFAgentDescription dfad = new DFAgentDescription();
        dfad.setName(getAID());
        //service no 1
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("answers");
        sd1.setName("hitchcock");

        //add them all
        dfad.addServices(sd1);

        try {
            DFService.register(this,dfad);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new HitchcockCyclicBehaviour(this));
        //doDelete();
    }
}

class HitchcockCyclicBehaviour extends CyclicBehaviour
{
    HitchcockServiceAgent agent;
    public HitchcockCyclicBehaviour(HitchcockServiceAgent agent)
    {
        this.agent = agent;
    }
    public void action()
    {
        MessageTemplate template = MessageTemplate.MatchOntology("hitchcock");
        ACLMessage message = agent.receive(template);
        if (message == null)
        {
            block();
        }
        else
        {
            //process the incoming message
            String content = message.getContent();
            ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            String response = "";
            try
            {
                response = agent.makeRequest("hitchcock",content);
            }
            catch (NumberFormatException ex)
            {
                response = ex.getMessage();
            }
            reply.setContent(response);
            agent.send(reply);
        }
    }
}
