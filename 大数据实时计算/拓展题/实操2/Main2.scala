import java.util.{Properties, UUID}

import org.apache.flink.api.common.serialization.{SimpleStringEncoder, SimpleStringSchema}
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.scala.function.ProcessAllWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object Main2 {
  /**
   * 输入的主题名称
   */
  val inputTopic = "mn_buy_ticket_1"
  /**
   * kafka地址
   */
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[String](inputTopic,
      new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)

    inputKafkaStream.map(x =>{
      (x.substring(x.indexOf("destination")+14,x.indexOf("username")-3),1)
    }).keyBy(0)
      .timeWindow(org.apache.flink.streaming.api.windowing.time.Time.seconds(60))
      .sum(1)
      .windowAll(TumblingProcessingTimeWindows.of(org.apache.flink.streaming.api.windowing.time.Time.seconds(10L)))
      .process(new ProcessAllWindowFunction[(String, Int), String, TimeWindow] {
        override def process(context: Context, elements: Iterable[(String, Int)], out: Collector[String]): Unit = {
          val top5 = elements.toSeq
            .sortBy(-_._2)
            .take(5)
            .zipWithIndex
            .map { case ((cityName,tickets ), idx) => s"   ${idx + 1}. $cityName: $tickets" }
            .mkString("\n")
          out.collect(("-" * 16) + "\n" + top5)
        }
      }).print()
    // inputKafkaStream.map(x => println(x))
    env.execute()
  }
}
