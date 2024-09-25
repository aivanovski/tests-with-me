package com.github.aivanovski.testswithme.shared.entitytree

import com.github.aivanovski.testswithme.shared.entitytree.model.{Entity, Node, Tree}
import com.github.aivanovski.testswithme.shared.entitytree.TreeExtensions.dumpToString
import munit.FunSuite

class TreeExtensionsTest extends FunSuite {

  test("dumpToString should print tree structure") {
    val root = Node(StringEntity("ROOT"))

    val nodeA  = Node(StringEntity("A"))
    val nodeB  = Node(StringEntity("B"))
    val nodeA1 = Node(StringEntity("A.1"))
    val nodeA2 = Node(StringEntity("A.2"))
    val nodeB1 = Node(StringEntity("B.1"))
    val nodeB2 = Node(StringEntity("B.2"))

    root.nodes.addOne(nodeA)
    root.nodes.addOne(nodeB)

    nodeA.nodes.addOne(nodeA1)
    nodeA.nodes.addOne(nodeA2)

    nodeB.nodes.addOne(nodeB1)
    nodeB.nodes.addOne(nodeB2)

    val tree = Tree(nodes = List(root))

    println(s"${tree.dumpToString()}")

    val expected =
      """StringEntity(ROOT)
         |  StringEntity(B)
         |    StringEntity(B.2)
         |    StringEntity(B.1)
         |  StringEntity(A)
         |    StringEntity(A.2)
         |    StringEntity(A.1)""".stripMargin

    assert(tree.dumpToString() == expected)
  }
}

case class StringEntity(
    override val id: String
) extends Entity
