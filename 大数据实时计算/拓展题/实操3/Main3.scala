import java.util.Properties

import com.bingocloud.{ClientConfiguration, Protocol}
import com.bingocloud.auth.BasicAWSCredentials
import com.bingocloud.services.s3.AmazonS3Client
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.nlpcn.commons.lang.util.IOUtil

import java.sql.DriverManager
object Main3 {
  //s3参数
  val accessKey = "F08312441E9AE64886F5"
  val secretKey = "W0IzNTM5OENCMzgyMTUyMjVDQTJCQ0VGNUI5QzVE"
  val endpoint = "http://10.16.0.1:81"
  val bucket = "zck"
  //要读取的文件
  val key = "demo.txt"

  //kafka参数
  val topic = "mn_buy_ticket_1_zck"
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val s3Content = readFromSql()
    produceToKafka(s3Content)
  }

  /**
   * 从s3中读取文件内容
   *
   * @return s3的文件内容
   */
//  def readFile(): String = {
//    val credentials = new BasicAWSCredentials(accessKey, secretKey)
//    val clientConfig = new ClientConfiguration()
//    clientConfig.setProtocol(Protocol.HTTP)
//    val amazonS3 = new AmazonS3Client(credentials, clientConfig)
//    amazonS3.setEndpoint(endpoint)
//    val s3Object = amazonS3.getObject(bucket, key)
//    IOUtil.getContent(s3Object.getObjectContent, "UTF-8")
//  }

  def readFromSql():String ={
    val url = "jdbc:hive2://bigdata115.depts.bingosoft.net:22115/user17_db"
    val properties = new Properties()
    properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver")
    properties.setProperty("user", "user17")
    properties.setProperty("password", "pass@bingo17")
    val connection = DriverManager.getConnection(url, properties)
    val statement = connection.createStatement()
    val sql = "show tables"
    val resultSet = statement.executeQuery(sql)
    val content = new StringBuilder()

    while (resultSet.next) {
      val tableName = resultSet.getString(1)
      //输出所有表名
      println("tableName：" + tableName)
      content.append(tableName + "\n")
    }
    content.toString()
  }

  /**
   * 把数据写入到kafka中
   *
   * @param s3Content 要写入的内容
   */
  def produceToKafka(s3Content: String): Unit = {
    val props = new Properties
    props.put("bootstrap.servers", bootstrapServers)
    props.put("acks", "all")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    val dataArr = s3Content.split("\n")
    for (s <- dataArr) {
      if (!s.trim.isEmpty) {
        val record = new ProducerRecord[String, String](topic, null, s)
        println("开始生产数据：" + s)
        producer.send(record)
      }
    }
    producer.flush()
    producer.close()
  }
}
