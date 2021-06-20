import java.io.{BufferedReader, File, FileReader, FileWriter}
import java.util
import java.util.{HashMap, LinkedList, Timer, TimerTask}

import com.bingocloud.auth.BasicAWSCredentials
import com.bingocloud.services.s3.AmazonS3Client
import com.bingocloud.{ClientConfiguration, Protocol}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.io.OutputFormat
import org.apache.flink.configuration.Configuration

import scala.util.parsing.json.JSON

class S3Writer(accessKey: String, secretKey: String, endpoint: String, bucket: String, keyPrefix: String, period: Int, filename: String, keyWordP: String) extends OutputFormat[String] {
  var keyWord : String = keyWordP //用于分类的关键字
  var timer: Timer = _
  var file: File = _
  var fileWriter: FileWriter = _
  var length = 0L
  var amazonS3: AmazonS3Client = _
  var content = new StringBuilder("")

  /**
   * 将内容按照 dest 分类，返回一个有序的内容
   * @param lines
   * @return
   */
  def group(lines: String):String = {
    var content = new StringBuilder("")
    var fullContent = new util.LinkedList[Tuple2[String, String]]
    lines.split("\n")
      .foreach(line => {
        var result = JSON.parseFull(line)
        var new_line: Tuple2[String, String] = null
        result.foreach(m => {
          m match {
            case map: Map[String, Any] => {
              map.get(keyWord) match {
                case Some(x) => new_line = (line, x.toString)
                case _ => new_line = (line, "")
              }
            }
            case _ => None
          }
          fullContent.add(new_line)
        })
      })

    var map: HashMap[String, LinkedList[String]] = new HashMap[String, LinkedList[String]]()
    var keyname = new util.LinkedList[String]()
    var j = 0
    var iter = fullContent.iterator()
    while (iter.hasNext) {
      var next = iter.next()
      if (!map.containsKey(next._2)) {
        var list = new util.LinkedList[String]()
        list.add(next._1)
        map.put(next._2, list)
        keyname.add(next._2)
      }
      else {
        map.get(next._2).add(next._1)
      }
    }

    //把关键字存在list数组中，遍历查找
    for(i <- 0 to keyname.size()-1){
      var key = keyname.get(i)
      content.append(key+":\n")
      var list = map.get(key)
      for(j <-0 to list.size()-1){
        content.append(list.get(j)+"\n")
      }
      content.append("\n")
    }
    content.toString()
  }


  def upload: Unit = {
    this.synchronized {
      if (length > 0) {
        //        fileWriter.close()
        // 重新排序
        fileWriter.write(group(content.toString))
        fileWriter.flush()
        val targetKey = keyPrefix + System.nanoTime()
        println("开始上传文件：%s 至 %s 桶的 %s 目录下".format(file.getAbsoluteFile, bucket, targetKey))
        amazonS3.putObject(bucket, targetKey, file)
        //        file = null
        //        fileWriter = null
        length = 0L
        content = new StringBuilder("")
      }
    }
  }

  override def configure(configuration: Configuration): Unit = {
    timer = new Timer("S3Writer")
    timer.schedule(new TimerTask() {
      def run(): Unit = {
        upload
      }
    }, 1000, period)
    val credentials = new BasicAWSCredentials(accessKey, secretKey)
    val clientConfig = new ClientConfiguration()
    clientConfig.setProtocol(Protocol.HTTP)
    amazonS3 = new AmazonS3Client(credentials, clientConfig)
    amazonS3.setEndpoint(endpoint)
    file = new File(filename)
    if (file.exists) {
      var bufferReader = new BufferedReader(new FileReader(file))
      var contentStr: String = null
      while ((contentStr = bufferReader.readLine()) != None && contentStr != null) {
        content.append(contentStr)
      }
      bufferReader.close()
    }

    fileWriter = new FileWriter(file)
  }

  override def open(taskNumber: Int, numTasks: Int): Unit = {
  }

  override def writeRecord(it: String): Unit = {
    this.synchronized {
      if (StringUtils.isNoneBlank(it)) {
        content.append(it + "\n")
        length += it.length
      }
    }
  }

  override def close(): Unit = {
    fileWriter.flush()
    fileWriter.close()
    timer.cancel()
  }
}
