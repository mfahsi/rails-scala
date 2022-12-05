package com.winova.core.graph

enum GraphError(val msg: String) extends Exception :
  case InvalidRoute extends GraphError("Invalid route definition")
  case NoSuchRoute extends GraphError("NO SUCH ROUTE")

