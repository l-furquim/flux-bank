variable "env" {
  type = string
}

variable "region" {
  type = string
}

variable "queues" {
  type = list(string)
  default = [
    "transaction-queue",
    "user-queue",
    "analytics-queue",
    "wallet-queue",
    "notification-queue"
  ]
}

variable "topics" {
  type = list(string)
  default = [
    "transaction-event-topic",
    "user-event-topic",
    "notification-event-topic",
    "wallet-event-topic",
    "analytics-event-topic"
  ]
}


variable "event-topics" {
  type = map(list(string))
  default = {
    wallet    = ["TransactionInitiated", "TransactionCompleted"],
    analytics = ["TransactionInitiated"]
  }
}
