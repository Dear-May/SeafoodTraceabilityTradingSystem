import logging
import os
import time
import requests
from pymilvus import Collection, connections

from image_vectoriz import img2feature
from mysql_operations import connect_to_mysql

log = logging.getLogger("console")


def fetch_goods_data(connection):
    """
    从商品表中获取数据
    """
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT id,info,image_path FROM t_image_search_image_add_log")
        goods = cursor.fetchall()
        return goods
    except Exception as e:
        log.error(f"Error fetching goods data: {e}")
        return []


def process_and_import_data(goods):
    """
    批量处理并导入数据到 Milvus 和 t_image_search_image_add_log
    """
    try:
        # 连接到 Milvus 集合
        connections.connect(host="127.0.0.1", port="19530")
        collection = Collection(name="image_features")
        batch_ids = []
        batch_features = []
        batch_timestamps = []

        # 连接到 MySQL 数据库
        connection = connect_to_mysql()

        # 创建临时目录存储下载的图片
        cursor = connection.cursor()
        os.makedirs("temp_images", exist_ok=True)

        for idx, good in enumerate(goods):
            id = good['id']
            goodName = good['info']
            show_url = good['image_path']

            # 下载图片到本地（如果是 URL）
            if show_url.startswith("http://") or show_url.startswith("https://"):
                try:
                    response = requests.get(show_url, stream=True, timeout=10)
                    if response.status_code == 200:
                        image_name = os.path.basename(show_url)
                        local_image_path = os.path.join("temp_images", image_name)
                        with open(local_image_path, "wb") as f:
                            f.write(response.content)
                        show_url = local_image_path
                    else:
                        log.error(f"Failed to download image: {show_url}")
                        continue
                except Exception as e:
                    log.error(f"Error downloading image {show_url}: {e}")
                    continue

            # 提取图像特征
            try:
                feature_vector = img2feature(show_url).flatten().tolist()
            except Exception as e:
                log.error(f"Error extracting features from image {show_url}: {e}")
                continue

            # 插入记录到 t_image_search_image_add_log
            try:
                # sql = "INSERT INTO t_image_search_image_add_log (image_path, info) VALUES (%s, %s)"
                # cursor.execute(sql, (show_url, goodName))
                # connection.commit()
                #
                # # 获取刚插入的记录 ID
                # cursor.execute("SELECT LAST_INSERT_ID()")
                # milvus_id = cursor.fetchone()[0]

                # 收集特征向量数据
                batch_ids.append(id)
                batch_features.append(feature_vector)
                batch_timestamps.append(int(time.time()))
            except Exception as e:
                log.error(f"Error inserting record to MySQL: {e}")
                continue

        # 批量插入到 Milvusand
        if batch_ids and batch_features and batch_timestamps:
            try:
                data1 = [
                    batch_ids,  # milvus_id 字段
                    batch_features,  # vectors 字段
                    batch_timestamps  # 时间戳字段
                ]
                collection.insert(data1)
                print('insert compete')
                log.info(f"Successfully inserted {len(batch_ids)} vectors into Milvus.")
            except Exception as e:
                log.error(f"Error inserting data into Milvus: {e}")

        log.info("Batch import completed.")
    except Exception as e:
        log.error(f"Error in process_and_import_data: {e}")


if __name__ == "__main__":
    # Step 1: 连接到数据库
    db_connection = connect_to_mysql()
    if not db_connection:
        exit()
    # Step 2: 获取商品数据
    goods_data = fetch_goods_data(db_connection)
    # Step 3: 处理并导入数据
    if goods_data:
        process_and_import_data(goods_data)

    # 关闭数据库连接
    db_connection.close()
