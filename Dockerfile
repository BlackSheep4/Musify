FROM python:3.12-alpine

# Crea el directorio de trabajo
WORKDIR /music

# Instala las dependencias necesarias
RUN apk add --no-cache \
    bash \
    build-base \
    curl \
    ffmpeg \
    git \
    py3-setuptools \
    py3-wheel && \
    pip install --no-cache-dir spotdl

# Expone el puerto de la interfaz web
EXPOSE 8800

VOLUME [ "/music" ]

ENTRYPOINT [ "spotdl" ]