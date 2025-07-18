import datetime
import logging
import time
from concurrent.futures import ThreadPoolExecutor

import redis
from pymilvus import Collection

from search.tools.image_vectoriz import img2feature
from search.tools.mysql_operations import connect_to_mysql
from search.tools.some_config_file import REDIS

log = logging.getLogger("console")
log_addimgs = logging.getLogger("console_addimgs")


def worker(datas):
    """
    线程任务：处理图像特征提取和插入 Milvus、MySQL 的操作。
    """
    try:
        # 初始化 Redis 客户端
        redis_cli = redis.Redis(
            host=REDIS.get("host"), port=REDIS.get("port"), password=REDIS.get("password"), db=REDIS.get("db")
        )

        collection = Collection(name="image_features")  # 连接 Milvus 集合
        batch_ids = []
        batch_features = []

        for data in datas:
            milvus_id, image_path, create_time = data

            # 基于 Redis 去重
            if redis_cli.zscore("image_search", str(milvus_id)):
                continue

            # 提取图像特征
            feature_vector = img2feature(image_path).flatten().tolist()
            batch_ids.append(milvus_id)
            batch_features.append(feature_vector)

            # Redis 中记录已处理的图像 ID
            redis_cli.zadd("image_search", {str(milvus_id): time.mktime(create_time.timetuple())})

        # 批量插入特征向量到 Milvus
        if batch_ids and batch_features:
            collection.insert([batch_ids, batch_features])
            log.info(f"Inserted {len(batch_ids)} vectors into Milvus.")

        # 更新 MySQL 状态为已加载
        if batch_ids:
            sql_update = """UPDATE t_image_search_image_add_log SET is_load=1 WHERE id=%s"""
            db168 = connect_to_mysql()
            cur168 = db168.cursor()
            cur168.executemany(sql_update, [(id,) for id in batch_ids])
            db168.commit()
            log.info(f"Updated MySQL status for {len(batch_ids)} records.")
    except Exception as e:
        log.error(f"Error in worker: {e}")


def main():
    """
    主任务：从 MySQL 中加载未处理的图像数据，按批次分配到线程池处理。
    """
    max_workers = 20  # 最大线程数
    pool = ThreadPoolExecutor(max_workers=max_workers, thread_name_prefix="Thread")
    task_list = []
    init_time = datetime.datetime.now() - datetime.timedelta(hours=13)
    create_time_init = "2025-01-01 00:00:00"

    while True:
        now = datetime.datetime.now()
        diff = now - init_time
        if diff.seconds > 3600:  # 每小时执行一次
            try:
                # 加载未处理的图像数据
                db168 = connect_to_mysql()
                cur168 = db168.cursor()
                sql = """SELECT id, image_path, create_time 
                         FROM t_image_search_image_add_log 
                         WHERE is_load=0 AND create_time >= %s 
                         ORDER BY create_time"""
                cur168.execute(sql, (create_time_init,))
                datas = cur168.fetchall()

                if datas:
                    create_time_init = datas[-1][2]

                    # 分配任务到线程池
                    while len(task_list) >= int(max_workers * 0.9):
                        task_list = [task for task in task_list if not task.done()]  # 移除已完成的任务
                    task_list.append(pool.submit(worker, datas))
                    log.info(f"Dispatched {len(datas)} records to workers.")

                init_time = now
            except Exception as e:
                log.error(f"Error in main loop: {e}")

        time.sleep(600)  # 等待 10 分钟后再检查


if __name__ == "__main__":
    main()
