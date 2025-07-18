import time

import numpy as np
from numpy import linalg as LA
from tensorflow.keras.applications.resnet50 import ResNet50
from tensorflow.keras.applications.resnet50 import preprocess_input
from tensorflow.keras.preprocessing import image

model = ResNet50(weights='imagenet')


def img2feature(img_path, input_dim=224):
    """
    功能：图像向量化
    """
    img = image.load_img(img_path, target_size=(input_dim, input_dim))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)
    x = model.predict(x)
    x = x / LA.norm(x)
    return x


if __name__ == "__main__":
    img_path = '../../images/0001000A.jpg'
    t0 = time.time()
    res = img2feature(img_path)
    print(time.time() - t0, res.shape)
