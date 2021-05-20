package indigo.shared.scenegraph.syntax

import indigo.shared.datatypes.Point
import indigo.shared.datatypes.Radians
import indigo.shared.datatypes.Vector2
import indigo.shared.datatypes.Depth
import indigo.shared.datatypes.Flip

trait Spatial[T: BasicSpatial]:
  extension (spatial: T)
    def moveTo(pt: Point): T =
      spatial.withPosition(pt)
    def moveTo(x: Int, y: Int): T =
      moveTo(Point(x, y))

    def moveBy(pt: Point): T
    def moveBy(x: Int, y: Int): T

    def rotateTo(angle: Radians): T
    def rotateBy(angle: Radians): T

    def scaleBy(amount: Vector2): T
    def scaleBy(x: Double, y: Double): T

    def transformTo(newPosition: Point, newRotation: Radians, newScale: Vector2): T
    def transformBy(positionDiff: Point, rotationDiff: Radians, scaleDiff: Vector2): T

    def withRef(newRef: Point): T
    def withRef(x: Int, y: Int): T

    def flipHorizontal(isFlipped: Boolean): T
    def flipVertical(isFlipped: Boolean): T

object Spatial:
  import scala.deriving.*
  import scala.compiletime.summonAll

  private def spatialSum[T: BasicSpatial](s: Mirror.SumOf[T], instances: => List[Spatial[?]]): Spatial[T] =
    def usingInstanceFor[O](input: T)(f: Spatial[input.type] => O): O =
      val ordx = s.ordinal(input)
      f(instances(ordx).asInstanceOf[Spatial[input.type]])

    new Spatial[T]:
      extension (spatial: T)
        def moveBy(pt: Point): T      = usingInstanceFor(spatial)(_.moveBy(spatial)(pt))
        def moveBy(x: Int, y: Int): T = usingInstanceFor(spatial)(_.moveBy(spatial)(x, y))

        def rotateTo(angle: Radians): T = usingInstanceFor(spatial)(_.rotateTo(spatial)(angle))
        def rotateBy(angle: Radians): T = usingInstanceFor(spatial)(_.rotateBy(spatial)(angle))

        def scaleBy(amount: Vector2): T      = usingInstanceFor(spatial)(_.scaleBy(spatial)(amount))
        def scaleBy(x: Double, y: Double): T = usingInstanceFor(spatial)(_.scaleBy(spatial)(x, y))

        def transformTo(newPosition: Point, newRotation: Radians, newScale: Vector2): T =
          usingInstanceFor(spatial)(_.transformTo(spatial)(newPosition, newRotation, newScale))
        def transformBy(positionDiff: Point, rotationDiff: Radians, scaleDiff: Vector2): T =
          usingInstanceFor(spatial)(_.transformBy(spatial)(positionDiff, rotationDiff, scaleDiff))

        def withRef(newRef: Point): T  = usingInstanceFor(spatial)(_.withRef(spatial)(newRef))
        def withRef(x: Int, y: Int): T = usingInstanceFor(spatial)(_.withRef(spatial)(x, y))

        def flipHorizontal(isFlipped: Boolean): T = usingInstanceFor(spatial)(_.flipHorizontal(spatial)(isFlipped))
        def flipVertical(isFlipped: Boolean): T   = usingInstanceFor(spatial)(_.flipVertical(spatial)(isFlipped))

  inline given derived[T: BasicSpatial](using m: Mirror.SumOf[T]): Spatial[T] =
    type SpatialInstances = Tuple.Map[m.MirroredElemTypes, Spatial]
    lazy val spatialInstances = summonAll[SpatialInstances].toList.asInstanceOf[List[Spatial[?]]]
    spatialSum(m, spatialInstances)
