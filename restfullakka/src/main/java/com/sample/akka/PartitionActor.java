package com.sample.akka;

import javax.annotation.Resource;
import javax.inject.Named;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.SmallestMailboxPool;

import com.sample.akka.factory.ServiceFactory;
import com.sample.akka.service.PartitionServiceImpl;
import com.sample.akka.service.PartitionService;


public class PartitionActor extends UntypedActor {
	
	
	 private ActorRef workerRouter;
	 
	
	 private PartitionService partitionService;
	 private Long totalItems;
	 private Long size;
	 
	 
	 private ServiceFactory serviceFactory;
	 
	public PartitionActor(String name, Long size, Long totalItems) {
		this.totalItems = totalItems;
		this.size = size;
		 workerRouter = getContext().system().
	                actorOf(Props.create(FileWriterActor.class).withRouter(new SmallestMailboxPool(5)));
	}


	@Override
	public void onReceive(Object message) throws Exception {
		
		partitionService =  new PartitionServiceImpl();
		Long cnt = partitionService.performPartition(workerRouter, message, getSelf(), totalItems, size);
		if (cnt != null) {
			getSender().tell(Boolean.TRUE, getSelf());
		}

	}
	
	

}
