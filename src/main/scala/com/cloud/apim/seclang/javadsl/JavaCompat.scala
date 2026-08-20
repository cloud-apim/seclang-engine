package com.cloud.apim.seclang.javadsl

import java.util.{List => JList, Map => JMap}

import scala.collection.JavaConverters._

/**
 * Conversion helpers used by the java DSL.
 *
 * Those conversions are written in scala because the java code needed to build immutable scala
 * collections (`toMap` and its `<:<` evidence) is not source compatible between scala 2.12 and 2.13.
 */
object JavaCompat {

  /** Converts a java map into an immutable scala map, `null` being handled as an empty map. */
  def toScalaMap[K, V](map: JMap[K, V]): Map[K, V] = {
    if (map == null) Map.empty[K, V] else map.asScala.toMap
  }

  /** Converts a java list into an immutable scala list, `null` being handled as an empty list. */
  def toScalaList[A](list: JList[A]): List[A] = {
    if (list == null) List.empty[A] else list.asScala.toList
  }

  /** Converts a java map of java lists into an immutable scala map of immutable scala lists. */
  def toScalaMapOfLists[K, V](map: JMap[K, JList[V]]): Map[K, List[V]] = {
    if (map == null) Map.empty[K, List[V]]
    else map.asScala.map { case (key, value) => (key, toScalaList(value)) }.toMap
  }
}
