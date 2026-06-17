FROM python:3.12-slim

WORKDIR /app

# 安装系统依赖（mysqlclient 等需要）
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    default-libmysqlclient-dev \
    pkg-config \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8889

CMD ["gunicorn", "--bind", "0.0.0.0:8889", "pythonapi.wsgi:application"]
