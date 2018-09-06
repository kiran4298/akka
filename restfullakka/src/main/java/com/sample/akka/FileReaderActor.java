package com.sample.akka;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.FileNotFoundException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.Broadcast;

import com.sample.model.Student;


public class FileReaderActor extends UntypedActor {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), "FileReaderActor");

    private ActorRef partitionActor;
    
   private String elementName;

    public FileReaderActor(String elementName, final Long size, final Long totalItems) {
		this.elementName = elementName;

    	partitionActor = this.getContext().actorOf(Props.create(PartitionActor.class,"partition", size, totalItems));
	}


    private void readfile(String fileName) throws FileNotFoundException, XMLStreamException, JAXBException, SAXException {
        // set up a StAX reader
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
		 XMLStreamReader xmlr = xmlif.createXMLStreamReader(FileReaderActor.class.getClassLoader().getResourceAsStream(fileName));

        //set up JAXB context
        JAXBContext jaxbContext = JAXBContext.newInstance(
                new Class[]{
                        Consumer.class
                });
        Unmarshaller um = jaxbContext.createUnmarshaller();
        getSchemaValidate(um);

        String actorPath = getSelf().path().name();

        // move to the root element and check its name.
        xmlr.nextTag();
        xmlr.require(START_ELEMENT, null, "students");

        // move to the first contact element.
        xmlr.nextTag();
        while (xmlr.getEventType() == START_ELEMENT) {

            xmlr.require(START_ELEMENT, null, elementName);
            // unmarshall one contact element into a JAXB Contact object
            Student student = (Student) um.unmarshal(xmlr);
            

            

            partitionActor.tell(student, getSelf());

            if (xmlr.getEventType() == CHARACTERS) {
                xmlr.next();
            }
            //break;
        }
    }

    private void getSchemaValidate(Unmarshaller um)
            throws SAXException, JAXBException {
        um.setEventHandler(new ValidationEventHandler() {

                               //@Override
                               public boolean handleEvent(ValidationEvent event) {
                                   System.out.println(event.getMessage());
                                   return true;
                               }
                           }

        );
    }

    @Override
    public void onReceive(Object message) throws Exception {
    	
        if (message instanceof String) {
            String fileName = (String) message;
           
            readfile(fileName);

        } else if (message instanceof Boolean) {
        	
            	if((Boolean)message){
                partitionActor.tell(new Broadcast(PoisonPill.getInstance()), getSelf());

            }
        }
       
    }
}
