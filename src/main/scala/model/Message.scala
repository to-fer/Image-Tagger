package model

abstract class Message(message: String)
case class ErrorMessage(message: String) extends Message(message)
case class NormalMessage(message: String) extends Message(message)
