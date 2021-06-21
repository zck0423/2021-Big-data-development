import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object Main1 {
  val target="b"
  var count = 0
  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //Linux or Mac:nc -l 9999
    //Windows:nc -l -p 9999
    val text = env.socketTextStream("localhost", 9999)

//    val stream = text.flatMap {
//      _.toLowerCase.split("\\W+") filter {
//        _.contains(target)
//      }
//    }.map{
//      ("发现目标："+_ )
//    }
//
    val stream = text.flatMap(line =>line.split(""))
    .filter(line =>line.contains(target))
    .map(_ => ("", 1))
    .keyBy(0)
    .timeWindow(Time.seconds(60))
    .sum(1)
    .print()
    //stream.print()
    env.execute("Window Stream WordCount")
  }

  case class WordCount(c: String, i: Int)

}

