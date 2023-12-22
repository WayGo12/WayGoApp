FROM python:3.9

COPY ./requirements.txt /app/requirements.txt

RUN pip install --no-cache-dir -r /app/requirements.txt

COPY . /app

WORKDIR /app

ENV PYTHONUNBUFFERED=1

ENV HOST 0.0.0.0

EXPOSE 5000

CMD ["gunicorn", "app:app", "--bind", "0.0.0.0:5000"]
