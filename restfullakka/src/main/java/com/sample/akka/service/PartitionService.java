package com.sample.akka.service;

import akka.actor.ActorRef;

public interface PartitionService {

	
	public Long performPartition(ActorRef actorRef, Object message, ActorRef self);
}
