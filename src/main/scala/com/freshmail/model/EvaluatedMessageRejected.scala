package com.freshmail.model

case class EvaluatedMessageRejected(messageId: String,
                                    wordCount: Seq[((String, Int), Int)])
