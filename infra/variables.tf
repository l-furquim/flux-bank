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
