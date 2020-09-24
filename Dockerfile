## Base image for multi-arch distribution
FROM ubuntu:18.04

ENV DEBIAN_FRONTEND="noninteractive"
ENV BUILD_DEPS \
    ca-certificates \
    openjdk-11-jdk \
    zip \
    python \
    python3 \
    git \
    bzip2 \
    build-essential \
    curl \
    openssh-client \
    
    net-tools \
    unzip

RUN apt-get update && apt-get install -y ${BUILD_DEPS} && rm -rf /var/lib/apt/lists/*

## Copy & extract ONOS binaries
RUN mkdir -p /root/onos
WORKDIR /root/onos
COPY onos.tar.gz .
RUN tar -xf onos.tar.gz --strip-components=1 &&\
 rm -f onos.tar.gz

RUN sed -i '/^export JAVA_OPTS.*/aexport JAVA_HOME="$(dirname $(dirname $(readlink -f $(which java))))"' bin/onos-service

ARG VERSION

LABEL org.label-schema.name="ONOS" \
      org.label-schema.description="SDN Controller" \
      org.label-schema.usage="http://wiki.onosproject.org" \
      org.label-schema.url="http://onosproject.org" \
      org.label-scheme.vendor="Open Networking Foundation" \
      org.label-schema.schema-version="1.0" \
      org.label-schema.image-version=$VERSION \
      org.label-schema.cmd="docker run -it --name --rm --privileged -e ONOS_APPS=gui2,openflow,de.uniba.ktr.crma,fwd -p 8181:8181 -p 8101:8101 -p 6653:6653 -p 6640:6640 -p 9876:9876 thanhledev/onos-multiarch" \
      maintainer="duy-thanh.le@stud.uni-bamberg.de"


# Ports
# 6653 - OpenFlow
# 6640 - OVSDB
# 8181 - GUI
# 8101 - ONOS CLI
# 9876 - ONOS intra-cluster communication
EXPOSE 6653 6640 8181 8101 9876

# Run ONOS
ENTRYPOINT ["./bin/onos-service"]
CMD ["server"]