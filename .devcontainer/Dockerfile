FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        podman \
        uidmap \
        slirp4netns \
        fuse-overlayfs \
        containernetworking-plugins \
        curl \
        ca-certificates \
        zip \
        unzip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
