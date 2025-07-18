import time

from pymilvus import (
    connections, FieldSchema, CollectionSchema, DataType,
    Collection, utility
)

from search.tools.image_vectoriz import img2feature

# Milvus 连接配置
MILVUS_HOST = "127.0.0.1"
MILVUS_PORT = "19530"
COLLECTION_NAME = "image_features"
dim = 1000
default_fields = [
    FieldSchema(name="milvus_id", dtype=DataType.INT64, is_primary=True),
    FieldSchema(name="feature", dtype=DataType.FLOAT_VECTOR, dim=dim),
    FieldSchema(name="create_time", dtype=DataType.INT64)
]


def create_table():
    # 连接 Milvus
    connections.connect(host=MILVUS_HOST, port=MILVUS_PORT)
    default_schema = CollectionSchema(fields=default_fields, description="test collection")
    print(f"\nCreate collection...")
    collection = Collection(name=COLLECTION_NAME, schema=default_schema)
    print(f"\nCreate index...")
    default_index = {"index_type": "FLAT", "params": {"nlist": 128}, "metric_type": "L2"}
    collection.create_index(field_name="feature", index_params=default_index)
    print(print(f"\nCreate index...is successful"))
    collection.load()


def insert_data(img_path, goods_id):
    connections.connect(host=MILVUS_HOST, port=MILVUS_PORT)
    default_schema = CollectionSchema(fields=default_fields, description="test collection")
    collection = Collection(name=COLLECTION_NAME, schema=default_schema)
    vectors = img2feature(img_path).tolist()[0]
    print(type(vectors), len(vectors))
    data1 = [
        [goods_id],
        [vectors],
        [int(time.time())]
    ]
    collection.insert(data1)
    print('insert compete')


def search_data(img_path):
    print('search')
    connections.connect(host=MILVUS_HOST, port=MILVUS_PORT)
    collection = Collection(name=COLLECTION_NAME)
    print('连接成功')

    # # 首次查询建立索引和load()
    # default_index = {"index_type": "FLAT", "params": {"nlist": 128}, "metric_type": "L2"}
    # print(f"\nCreate index...")
    # collection.create_index(field_name="feature", index_params=default_index)
    # print(print(f"\nCreate index...is OKOKOKOKOK"))
    # collection.load()
    # exit()

    vectors = img2feature(img_path).tolist()[0]

    topK = 120
    search_params = {"metric_type": "L2", "params": {"nprobe": 10}}

    res = collection.search(
        [vectors],
        "feature",
        search_params,
        topK,
        "create_time > {}".format(0),
        output_fields=["milvus_id"]
    )
    print('>>>', res)
    for hits in res:
        print(len(hits))
        for hit in hits:
            print(hit)
    print('查询结束')
    return res


def show_nums():
    connections.connect(host=MILVUS_HOST, port=MILVUS_PORT)
    collection = Collection(name=COLLECTION_NAME)
    print('ok')
    print(collection.num_entities)


def delete_table():
    connections.connect(host=MILVUS_HOST, port=MILVUS_PORT)
    default_schema = CollectionSchema(fields=default_fields, description="test collection")
    collection = Collection(name=COLLECTION_NAME, schema=default_schema)
    print('>>>', utility.has_collection(COLLECTION_NAME))
    collection.drop()
    print('>>>', utility.has_collection(COLLECTION_NAME))


if __name__ == "__main__":
    t1 = time.time()
    # create_table()
    img_path = "temp_images/0001000A.jpg"
    # insert_data(img_path)
    search_data(img_path)
    show_nums()
    # delete_table()
    print('time cost: {}'.format(time.time() - t1))
