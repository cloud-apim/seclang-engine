package com.cloud.apim.seclang.impl.model

sealed trait Directive { def raw: String }

final case class Rule(
    variables: List[VariableSelector],
    operator: Operator,
    actions: List[Action],
    raw: String
) extends Directive {

  lazy val id: Option[Int] =
    actions.collectFirst { case Action.Id(v) => v }

  lazy val phase: Int =
    actions.collectFirst { case Action.Phase(p) => p }.getOrElse(2)

  lazy val isChain: Boolean =
    actions.exists(_ == Action.Chain)
}

final case class SecAction(actions: List[Action], raw: String) extends Directive

final case class RuleRemoveById(ids: List[Int], raw: String) extends Directive

final case class Marker(name: String, raw: String) extends Directive

final case class Comment(raw: String) extends Directive
final case class Unknown(raw: String) extends Directive

// -------- variables --------
final case class VariableSelector(collection: String, key: Option[String] = None) {
  override def toString: String = key match {
    case None    => collection
    case Some(k) => s"$collection:$k"
  }
}

// -------- operators --------
sealed trait Operator
object Operator {
  final case class Rx(pattern: String) extends Operator
  final case class Contains(value: String) extends Operator
  final case class Streq(value: String) extends Operator
  final case class Pm(values: List[String]) extends Operator
  final case class UnconditionalTrue() extends Operator // @unconditionalMatch
  final case class Raw(op: String, arg: String) extends Operator // fallback
}

// -------- actions --------
sealed trait Action
object Action {
  final case class Id(value: Int) extends Action
  final case class Phase(value: Int) extends Action
  final case class Msg(value: String) extends Action
  final case class Status(code: Int) extends Action
  final case class Severity(value: Int) extends Action

  // disruptive
  case object Deny extends Action
  case object Drop extends Action
  case object Pass extends Action
  case object Log extends Action
  case object NoLog extends Action

  // chaining / flow
  case object Chain extends Action
  final case class SkipAfter(marker: String) extends Action

  // transformations
  final case class Transform(name: String) extends Action

  // controls (runtime)
  final case class CtlRuleRemoveById(id: Int) extends Action

  // variables / scores (minimal)
  final case class SetVar(expr: String) extends Action

  // fallback
  final case class Raw(name: String, value: Option[String]) extends Action
}

// -------- evaluation context --------
final case class RequestContext(
    method: String,
    uri: String,
    headers: Map[String, List[String]],
    query: Map[String, List[String]],
    body: Option[String] = None
)

// -------- result --------
sealed trait Disposition
object Disposition {
  case object Continue extends Disposition
  final case class Block(status: Int, msg: Option[String], ruleId: Option[Int]) extends Disposition
}

final case class MatchEvent(
    ruleId: Option[Int],
    msg: Option[String],
    phase: Int,
    raw: String
)

final case class EngineResult(
    disposition: Disposition,
    events: List[MatchEvent]
)

