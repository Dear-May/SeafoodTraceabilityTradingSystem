from django.urls import path

from . import views
from .image_search_views import AddImage, SearchImage

urlpatterns = [
    path('', views.index, name='index'),
    path('index/', views.index, name='index'),  # 首页
    path("test/", views.test, name="test"),
    path('add_image/', AddImage.as_view(), name='add_image'),  # 图像添加服务
    path('search_image/', SearchImage.as_view(), name='search_image'),  # 图像搜索服务
]
