import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}


class MysqlWriter {

  val url = "jdbc:mysql://localhost:3306/mysql?serverTimezone=GMT"
  //Class.forName("com.mysql.jdbc.Driver")
  //驱动名称
  val driver = "com.mysql.jdbc.Driver"

  //用户名
  val username = "root"
  //密码
  val password = "912342"

  var insertSql: PreparedStatement=_

  def upload : Unit = {
    
    val  connection = getConnection()
    try {
      val statement = connection.createStatement()
      insertSql = connection.prepareStatement("insert into test values(\"zzz\",20)")
      //statement.execute(sql)

    }
    finally {
      connection.close()
    }
  }

  def getConnection(): Connection = {
    DriverManager.getConnection(url, username, password)
  }

  def close(conn: Connection): Unit = {
    try {
      if (!conn.isClosed() || conn != null) {
        conn.close()
      }
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
      }
    }
  }

}

case class SensorReading(str: String, toLong: Long, toDouble: Double)