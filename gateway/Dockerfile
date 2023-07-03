FROM registry.access.redhat.com/ubi9/ubi-minimal:latest
WORKDIR /app
COPY build/native/nativeCompile/* ./
ENTRYPOINT ["/app/gateway"]