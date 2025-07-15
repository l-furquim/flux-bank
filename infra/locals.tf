locals {
  service_topic_arns = {
    for svc in var.queues : svc => (
      contains(
        lookup(var.event_topics, svc, []),
        "*"
      )
      ? [for t in values(aws_sns_topic.sns_topics) : t.arn]
      : [for evt in lookup(var.event_topics, svc, []) : aws_sns_topic.sns_topics[evt].arn]
    )
  }

  subscriptions = {
    for pair in flatten([
      for svc, evts in var.event_topics : [
        for evt in(contains(evts, "*") ? var.topics : evts) : {
          key = "${svc}-${evt}"
          svc = svc
          evt = evt
        }
      ]
      ]) : pair.key => {
      svc = pair.svc
      evt = pair.evt
    }
  }
}
