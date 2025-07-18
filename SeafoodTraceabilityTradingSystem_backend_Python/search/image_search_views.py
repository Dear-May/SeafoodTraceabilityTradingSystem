import logging
import os
import secrets
import time

import requests
from django.http import JsonResponse
from pymilvus import connections, Collection
from rest_framework.views import APIView as View
from rest_framework.response import Response
from rest_framework import status

from search.tools.image_vectoriz import img2feature
from search.tools.mysql_operations import connect_to_mysql
from search.tools.milvus_operations import search_data

log = logging.getLogger("console")

# 连接到 Milvus
MILVUS_HOST = "127.0.0.1"
MILVUS_PORT = "19530"
connections.connect(host=MILVUS_HOST, port=MILVUS_PORT)


class AddImage(View):
    def post(self, request):
        try:
            # 获取请求数据
            image_info = request.POST.get('image_info')
            image_path = request.POST.get('image_path')

            # 判断 image_path 是否为 URL
            if image_path.startswith("http://") or image_path.startswith("https://"):
                # 下载图片到本地
                response = requests.get(image_path, stream=True)
                if response.status_code == 200:
                    image_name = os.path.basename(image_path)
                    save_path = os.path.join("temp_images", image_name)
                    os.makedirs("temp_images", exist_ok=True)
                    with open(save_path, "wb") as f:
                        f.write(response.content)
                    image_path = save_path
                else:
                    raise ValueError(f"Failed to download image from URL: {image_path}")

            # 提取图像特征向量
            feature_vector = img2feature(image_path).flatten().tolist()

            # 连接到 MySQL 数据库
            db = connect_to_mysql()
            cur = db.cursor()

            # 插入图像信息到 MySQL
            sql = "INSERT INTO t_image_search_image_add_log(image_path, info) VALUES (%s, %s)"
            cur.execute(sql, (image_path, image_info))
            db.commit()

            # 获取插入记录的 ID
            cur.execute("SELECT LAST_INSERT_ID()")
            milvus_id = cur.fetchone()[0]

            # 插入特征向量到 Milvus
            collection = Collection(name="image_features")
            collection.insert([[milvus_id], [feature_vector], [int(time.time())]])
            log.info(f"Image added successfully: {image_path} with ID {milvus_id}")

            return SuccessAPIResponse(msg="Image added successfully", data={"milvus_id": milvus_id})
        except Exception as e:
            log.error(f"Failed to add image: {e}")
            return ErrorAPIResponse(msg="Failed to add image")


class SearchImage(View):
    def post(self, request):
        try:
            # 检查是否上传了文件
            if 'image' not in request.FILES:
                return Response({"error": "No image file uploaded."}, status=status.HTTP_400_BAD_REQUEST)

            # 获取上传的文件
            uploaded_file = request.FILES['image']
            temp_dir = "temp_images"
            os.makedirs(temp_dir, exist_ok=True)

            # 将上传的文件保存到临时目录
            temp_path = os.path.join(temp_dir, uploaded_file.name)
            with open(temp_path, "wb") as f:
                for chunk in uploaded_file.chunks():
                    f.write(chunk)

            log.info(f"Image uploaded and saved at {temp_path}")

            results = search_data(temp_path)

            # 格式化搜索结果
            search_results = [{"milvus_id": hit.id, "distance": hit.distance} for hit in results[0]]
            log.info(f"Search completed for image: {temp_path}, results: {search_results}")
            token = secrets.token_urlsafe(16)
            data = {'status': 'success', 'data': search_results, 'token': token}
            return JsonResponse(data)
        except Exception as e:
            log.error(f"Failed to search image: {e}")
            data = {'status': 'error', 'data': e}
            return JsonResponse(data, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class SuccessAPIResponse:
    def __init__(self, msg, data):
        self.msg = msg
        self.data = data

    def __repr__(self):
        return f"SuccessAPIResponse(msg={self.msg}, data={self.data})"


class ErrorAPIResponse:
    def __init__(self, msg, error_code=500):
        self.msg = msg
        self.error_code = error_code

    def __repr__(self):
        return f"ErrorAPIResponse(msg={self.msg}, error_code={self.error_code})"
