package com.sample.akka.service;

import java.util.ArrayList;
import java.util.List;

import com.sample.model.Student;

import akka.actor.ActorRef;

public class PartitionServiceImpl implements PartitionService {

	 private Long cont = new Long(0);	
	 private ActorRef sender;
	
	public Long performPartition(ActorRef workerRouter, Object message, ActorRef self) {
		
		if(message instanceof Student){
			
			cont++;
			workerRouter.tell((Student)message, self);
			
			if(cont ==7){
			return cont;
				
			}
		}
		return null;
	}


	/**
	 * @return the self
	 */
	public ActorRef getSelf() {
		return self;
	}

	/**
	 * @param self the self to set
	 */
	public void setSelf(ActorRef self) {
		this.self = self;
	}

}
