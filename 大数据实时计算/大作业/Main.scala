import java.util.{Properties, UUID}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.util.parsing.json.JSON
object Main {
  val accessKey = "F08312441E9AE64886F5"
  val secretKey = "W0IzNTM5OENCMzgyMTUyMjVDQTJCQ0VGNUI5QzVE"
  //s3地址
  val endpoint = "http://10.16.0.1:81"
  val bucket = "zck"
  //上传文件的路径前缀
  val keyPrefix = "upload/"
  //上传数据间隔 单位毫秒
  val period = 30000
  //输入的kafka主题名称
  val inputTopic = "mn_buy_ticket_1"
  //kafka地址
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"
  //val filename = "test"+System.nanoTime() + ".txt"
  def main(args: Array[String]): Unit = {

    //处理输入
    print("请输入读取的文件名称: ")
    val key = Console.readLine()
    val temp = new AcceptData(accessKey,secretKey,endpoint,bucket,key)
    val s3Content = temp.readFile()
    temp.produceToKafka(s3Content)

    print("分类依据的关键字名称: ")
    val keyWordP = Console.readLine()

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)
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


    // 把分类好的文件从flink 传至s3
    inputKafkaStream.writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, keyPrefix, period, key,keyWordP ))
    env.execute()

  }
}
