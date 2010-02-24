package com.example.service

import java.util.List
import javax.jdo.PersistenceManager
import com.example.model.Greeting
import com.google.inject.{Inject, Provider}


class TypesafeGreetingQueries @Inject()(pmProvider: Provider[PersistenceManager]) extends JdoGreetingQueries(pmProvider)
{
    override def latest(max: Int): List[Greeting] = super.latest(max)
}


