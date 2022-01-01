package co.blocke.scalajack2
package model

import codec.*
import factories.*
import scala.util.{ Success, Try }
import co.blocke.scala_reflection.*
import co.blocke.scala_reflection.impl.SelfRefRType

object CodecCache {


  val StandardFactories: List[CodecFactory] =
    List(
//      BigDecimalTypeAdapterFactory,
//      BigIntTypeAdapterFactory,
//      BinaryTypeAdapterFactory,
      BooleanCodecFactory,
//      ByteTypeAdapterFactory,
//      CharTypeAdapterFactory,
//      DoubleTypeAdapterFactory,
//      FloatTypeAdapterFactory,
      IntCodecFactory,
//      LongTypeAdapterFactory,
//      ShortTypeAdapterFactory,
      StringCodecFactory,
//      OptionTypeAdapterFactory,
//      TryTypeAdapterFactory,
//      TupleTypeAdapterFactory,
//      EitherTypeAdapterFactory, // Either must precede SealedTraitTypeAdapter
//      UnionTypeAdapterFactory,
//      IntersectionTypeAdapterFactory,
//      ArrayTypeAdapterFactory,
//      EnumTypeAdapterFactory,
//      UUIDTypeAdapterFactory,
      CollectionCodecFactory

      // WARNING: These two must precede CaseClassTypeAdapter in this list or all
      //     ValueClasses will be interpreted as case classes, and case objects
      //     will likewise be hidden (interpreted as regular classes).
//      SealedTraitTypeAdapterFactory,
//      ValueClassTypeAdapterFactory,
//      ScalaClassTypeAdapterFactory,

//      TraitTypeAdapterFactory,
//      AnyTypeAdapterFactory,
//      JavaBigDecimalTypeAdapterFactory,
//      JavaBigIntegerTypeAdapterFactory,
//      JavaBooleanTypeAdapterFactory,
//      JavaByteTypeAdapterFactory,
//      JavaCharacterTypeAdapterFactory,
//      JavaDoubleTypeAdapterFactory,
//      JavaFloatTypeAdapterFactory,
//      JavaIntegerTypeAdapterFactory,
//      JavaLongTypeAdapterFactory,
//      JavaNumberTypeAdapterFactory,
//      JavaShortTypeAdapterFactory,
//      DurationTypeAdapterFactory,
//      InstantTypeAdapterFactory,
//      LocalDateTimeTypeAdapterFactory,
//      LocalDateTypeAdapterFactory,
//      LocalTimeTypeAdapterFactory,
//      OffsetDateTimeTypeAdapterFactory,
//      OffsetTimeTypeAdapterFactory,
//      PeriodTypeAdapterFactory,
//      ZonedDateTimeTypeAdapterFactory,
//      JavaClassTypeAdapterFactory
    )
}

case class CodecCache(
                       jackFlavor: JackFlavor[_],
                       factories:  List[CodecFactory]):

  sealed trait Phase
  case object Uninitialized extends Phase
  case object Initializing extends Phase
  case class Initialized(codecAttempt: Try[Codec[_]]) extends Phase

  val selfCache = this

  /*
  class TypeEntry(tpe: RType):
    @volatile
    private var phase: Phase = Uninitialized
    // println(s"--> TACache (${typeEntries.size}) add [${tpe.name}]")

    def codec: Codec[_] =
      val attempt =
        phase match {
          case Initialized(a) => a

          case Uninitialized | Initializing =>
            synchronized {
              phase match {
                case Uninitialized =>
                  phase = Initializing
                  val codecAttempt = Try {
                    val foundFactory = factories.find(_.matches(tpe)).get
                    foundFactory.makeCodec(tpe)(selfCache)
                  }
                  phase = Initialized(codecAttempt)
                  codecAttempt

                case Initializing =>
                  Success(LazyCodec(CodecCache.this, tpe))

                case Initialized(a) =>
                  a
              }
            }
        }
      attempt.get
  */

  private val typeEntries = scala.collection.mutable.LongMap.empty[()=>Codec[_]]

  def withFactory(factory: CodecFactory): CodecCache =
    copy(factories = factories :+ factory)


  def of(concreteType: RType): Codec[_] =
    typeEntries.getOrElse(concreteType.hashCode, {
      val newEntry = concreteType match {
        //        case AnySelfRef      => new TypeEntry(AnyRType)
        //        case s: SelfRefRType => new TypeEntry(RType.of(s.infoClass))
        case s: SelfRefRType =>
          val t = RType.of(s.infoClass)
          val foundFactory = factories.find(_.matches(t)).get
          foundFactory.makeCodec(t)(selfCache)
        case s               =>
          val foundFactory = factories.find(_.matches(s)).get
          foundFactory.makeCodec(s)(selfCache)
      }
      typeEntries.put(concreteType.hashCode, newEntry)
      newEntry
    })()

  inline def of[T]: Codec[T] =
    of(RType.of[T]).asInstanceOf[Codec[T]]

  val self = this

  /*
//  object ConcreteTypeEntryFactory extends java.util.function.Function[RType, TypeEntry]:
  object ConcreteTypeEntryFactory extends java.util.function.Function[RType, ()=>Codec[_]]:
    private val AnyRType = RType.of[Any]
    private val AnySelfRef = SelfRefRType("scala.Any")
    override def apply(concrete: RType): ()=>Codec[_] =
      concrete match {
//        case AnySelfRef      => new TypeEntry(AnyRType)
//        case s: SelfRefRType => new TypeEntry(RType.of(s.infoClass))
        case s: SelfRefRType =>
          val t = RType.of(s.infoClass)
          val foundFactory = factories.find(_.matches(t)).get
          foundFactory.makeCodec(t)(selfCache)
//        case s               => new TypeEntry(s)
        case s               =>
          val foundFactory = factories.find(_.matches(s)).get
          foundFactory.makeCodec(s)(selfCache)
      }
*/