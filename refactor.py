import os

BASE = r'D:\Cache\SeafoodTraceabilityTradingSystem\SeafoodTraceabilityTradingSystem_backend_Java'
JAVA_BASE = os.path.join(BASE, 'src', 'main', 'java', 'com', 'shopping_c_backend', 'shoppping_c_backend')
SERVICE = os.path.join(JAVA_BASE, 'Service')
CONTROLLER = os.path.join(JAVA_BASE, 'Controller')
VO = os.path.join(JAVA_BASE, 'Vo')
CONFIG = os.path.join(JAVA_BASE, 'Config')
UTIL = os.path.join(JAVA_BASE, 'Util')
RESOURCE = os.path.join(BASE, 'src', 'main', 'resources')

print('Base paths configured OK')
