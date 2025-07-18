import mysql.connector
from pymilvus.exceptions import ConnectionNotExistException

# MySQL 配置
MYSQL_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "root",
    "database": "oceanwave"
}


def connect_to_mysql():
    """
    功能：连接到 MySQL 数据库
    """
    db = mysql.connector.connect(**MYSQL_CONFIG)
    if db.is_connected():
        print("Connected to MySQL!")
        return db
    else:
        raise ConnectionNotExistException("连接失败: 无法建立有效连接")


def insert_image_metadata(milvus_id, image_path, source="unknown"):
    """
    功能：插入图像元数据到 image_metadata 表
    """
    db = connect_to_mysql()
    cursor = db.cursor()
    sql = "INSERT INTO image_metadata (milvus_id, image_path, source) VALUES (%s, %s, %s)"
    val = (milvus_id, image_path, source)
    cursor.execute(sql, val)
    db.commit()
    print(f"Image metadata inserted: {image_path}")
    cursor.close()
    db.close()


def insert_add_log(image_path):
    """
    功能：插入图像添加日志到 image_add_log 表
    """
    db = connect_to_mysql()
    cursor = db.cursor()
    sql = "INSERT INTO image_add_log (image_path) VALUES (%s)"
    val = (image_path,)
    cursor.execute(sql, val)
    db.commit()
    print(f"Add log inserted: {image_path}")
    cursor.close()
    db.close()


if __name__ == "__main__":
    # 示例：插入数据
    insert_image_metadata(123456789, "../../images/0001000A.jpg", "example_source")
    insert_add_log("../../images/0001000A.jpg")
