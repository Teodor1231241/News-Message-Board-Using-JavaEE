/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ejb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Teddy
 */
@JMSDestinationDefinition(name = "java:app/jms/NewMessage", interfaceName = "javax.jms.Queue", resourceAdapter = "jmsra", destinationName = "NewMessage")
@MessageDriven(mappedName = "jms/NewMessage", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:app/jms/NewMessage"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class NewMessage implements MessageListener {
        @Resource
    private MessageDrivenContext mdc;
    @PersistenceContext(unitName = "NewsMessage-ejbPU")
    private EntityManager em;
    public NewMessage() {
    }
    
    @Override
    public void onMessage(Message message) {
ObjectMessage msg = null;
try {
if (message instanceof ObjectMessage) {
msg = (ObjectMessage) message;
NewsEntity e = (NewsEntity) msg.getObject();
save(e);
}
} catch (JMSException e) {
e.printStackTrace();
mdc.setRollbackOnly();
} catch (Throwable te) {
te.printStackTrace();
}
    }
    public void save(Object object) {
        em.persist(object);
    }
    
    
}