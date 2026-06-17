import logging
import os
import secrets
import tempfile
import time

import requests
from pymilvus import connections, Collection
from rest_framework.views import APIView as View
from rest_framework.response import Response
from rest_framework import status

from search.tools.image_vectoriz import img2feature
from search.tools.mysql_operations import connect_to_mysql
from search.tools.milvus_operations import search_data

log = logging.getLogger('console')

# --- Milvus 连接辅助函数 ---
_milvus_host = None
_milvus_port = None


def get_milvus_connection():
    \"\"\"延迟获取 Milvus 连接（单例模式）\"\"\"
    global _milvus_host, _milvus_port
    host = os.getenv('MILVUS_HOST', '127.0.0.1')
    port = os.getenv('MILVUS_PORT', '19530')
    if host != _milvus_host or port != _milvus_port:
        connections.connect(host=host, port=port)
        _milvus_host = host
        _milvus_port = port
    return host, port


def ensure_milvus_connected():
    \"\"\"确保 Milvus 已连接\"\"\"
    get_milvus_connection()


class SuccessAPIResponse(Response):
    def __init__(self, msg='Success', data=None, status_code=status.HTTP_200_OK):
        body = {'status': 'success', 'msg': msg, 'data': data}
        super().__init__(body, status=status_code)


class ErrorAPIResponse(Response):
    def __init__(self, msg='Error', errors=None, status_code=status.HTTP_500_INTERNAL_SERVER_ERROR):
        body = {'status': 'error', 'msg': msg, 'errors': errors}
        super().__init__(body, status=status_code)


class AddImage(View):
    def post(self, request):
        temp_paths = []
        try:
            # 获取请求数据
            image_info = request.POST.get('image_info')
            image_path = request.POST.get('image_path')

            # 判断 image_path 是否为 URL
            if image_path.startswith('http://') or image_path.startswith('https://'):
                # 下载图片到临时文件
                response = requests.get(image_path, stream=True)
                response.raise_for_status()
                suffix = os.path.splitext(os.path.basename(image_path))[1] or '.jpg'
                with tempfile.NamedTemporaryFile(delete=False, suffix=suffix, dir='temp_images') as tmp:
                    tmp.write(response.content)
                    image_path = tmp.name
                    temp_paths.append(tmp.name)
            else:
                if not os.path.exists(image_path):
                    raise FileNotFoundError(f'Image path not found: {image_path}')

            # 确保 Milvus 连接
            ensure_milvus_connected()

            # 提取图像特征向量
            feature_vector = img2feature(image_path).flatten().tolist()

            # 连接到 MySQL 数据库
            db = connect_to_mysql()
            cur = db.cursor()

            # 插入图像信息到 MySQL
            sql = 'INSERT INTO t_image_search_image_add_log(image_path, info) VALUES (%s, %s)'
            cur.execute(sql, (image_path, image_info))
            db.commit()

            # 获取插入记录的 ID
            cur.execute('SELECT LAST_INSERT_ID()')
            milvus_id = cur.fetchone()[0]
            cur.close()
            db.close()

            # 插入特征向量到 Milvus
            collection = Collection(name='image_features')
            collection.insert([[milvus_id], [feature_vector], [int(time.time())]])
            log.info(f'Image added successfully: {image_path} with ID {milvus_id}')

            return SuccessAPIResponse(msg='Image added successfully', data={'milvus_id': milvus_id})
        except Exception as e:
            log.error(f'Failed to add image: {e}')
            return ErrorAPIResponse(msg='Failed to add image')
        finally:
            # 清理临时文件
            for p in temp_paths:
                try:
                    if os.path.exists(p):
                        os.remove(p)
                except OSError:
                    pass


class SearchImage(View):
    def post(self, request):
        temp_path = None
        try:
            # 检查是否上传了文件
            if 'image' not in request.FILES:
                return Response({'error': 'No image file uploaded.'}, status=status.HTTP_400_BAD_REQUEST)

            # 获取上传的文件
            uploaded_file = request.FILES['image']
            os.makedirs('temp_images', exist_ok=True)

            # 将上传的文件保存到临时文件
            suffix = os.path.splitext(uploaded_file.name)[1] or '.jpg'
            with tempfile.NamedTemporaryFile(delete=False, suffix=suffix, dir='temp_images') as tmp:
                for chunk in uploaded_file.chunks():
                    tmp.write(chunk)
                temp_path = tmp.name

            log.info(f'Image uploaded and saved at {temp_path}')

            # 确保 Milvus 连接
            ensure_milvus_connected()

            results = search_data(temp_path)

            # 格式化搜索结果
            search_results = [{'milvus_id': hit.id, 'distance': hit.distance} for hit in results[0]]
            log.info(f'Search completed for image: {temp_path}, results: {search_results}')
            token = secrets.token_urlsafe(16)
            return Response({'status': 'success', 'data': search_results, 'token': token})
        except Exception as e:
            log.error(f'Failed to search image: {e}')
            return Response({'status': 'error', 'data': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        finally:
            # 清理临时文件
            if temp_path and os.path.exists(temp_path):
                try:
                    os.remove(temp_path)
                except OSError:
                    pass
