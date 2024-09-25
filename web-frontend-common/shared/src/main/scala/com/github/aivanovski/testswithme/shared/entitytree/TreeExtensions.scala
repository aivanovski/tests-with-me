package com.github.aivanovski.testswithme.shared.entitytree

import com.github.aivanovski.testswithme.shared.entitytree.model.{Node, Tree}
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object TreeExtensions {

  extension (tree: Tree)
    def dumpToString(
        formatter: (Node) => String = (n) => n.entity.toString
    ): String = {
      val lines = ArrayBuffer[String]()

      val visitor: (Int, Node) => Unit = { (depth, node) =>
        val indent = "  ".repeat(depth)

        val name = formatter.apply(node)
        lines.addOne(s"$indent$name")
      }

      for (node <- tree.nodes) {
        node.visitWithDepth(visitor)
      }

      lines.mkString(sep = "\n")
    }

  extension (node: Node)
    def visitWithDepth(
        visitor: (Int, Node) => Unit
    ): Unit = {
      val stack = mutable.Stack[(Int, Node)]()
      stack.push((0, node))

      while (stack.nonEmpty) {
        val (depth, currentNode) = stack.pop()

        visitor.apply(depth, currentNode)

        for (child <- currentNode.nodes) {
          stack.push((depth + 1, child))
        }
      }
    }
}
