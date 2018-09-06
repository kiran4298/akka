package com.sample.rest;

import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.springframework.beans.factory.annotation.Autowired;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;

import com.sample.akka.FileReaderActor;
import com.typesafe.config.ConfigFactory;

@Path("sample")
public class SimpleService {

    @Autowired
    private ActorSystem actorSystem;
    
    public SimpleService(){
    	
    }
  
    @Path("akka/{filename}")
    @GET
    public String getAkka(@PathParam("filename") String filename) {
    	
    	final String elementName = new String("student");
    	final long size = new Long(3);
    	final Long totalItems = new Long(6);
    		
         ActorRef reader = actorSystem.actorOf(Props.create(FileReaderActor.class, elementName, size, totalItems));
        	 reader.tell(filename, reader);
        }

       
        return "Hello Akka";
    }

}
