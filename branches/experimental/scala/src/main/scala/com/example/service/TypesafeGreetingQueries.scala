package com.example.service

import com.example.model.Greeting
import java.util.List

class TypesafeGreetingQueries extends GreetingQueries
{
    override def latest(max: Int) : List[Greeting] = super.latest(max)
}
