package model

import event.Observable

class MessageModel extends Observable {
  private var _message: Message = _

  def message_=(msg: Message): Unit = {
    _message = msg
    notifyObservers()
  }

  def message: Message = _message
}
