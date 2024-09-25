package com.github.aivanovski.testswithme.shared.entitytree.model

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Node(
    entity: Entity,
    nodes: ListBuffer[Node] = ListBuffer()
)
