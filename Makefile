PLATFORMS = linux/amd64,linux/arm64, # linux/arm/v7,linux/ppc64le,linux/s390x will be added when applicable
ONOS_VERSION=2.4.0
VERSION = $(shell cat VERSION)
BINFMT = a7996909642ee92942dcd6cff44b9b95f08dad64
REPO = onos-multiarch
#DOCKER_USER=thanhledev
#DOCKER_PASS=5174l0rD

.PHONY: all init build clean

all: init build clean


init: clean
	#-we download the onos source code & copy crma application to the required location
	@echo "Init function"
	@mkdir -p onos && wget https://github.com/opennetworkinglab/onos/archive/${ONOS_VERSION}.tar.gz && tar xvf ${ONOS_VERSION}.tar.gz -C ./onos --strip-components=1
	@cp -r crma ./onos/apps
	#-edit necessary files
	@sed -i '/^APP_MAP = /a\    \"//apps/crma:onos-apps-crma-oar": [],' ./onos/tools/build/bazel/modules.bzl
	@sed -i '/io_netty_netty_tcnative_boringssl/c\    \"io_netty_netty_tcnative_boringssl\": \"https://github.com/thanhledev/netty-tcnative-boringssl-static-fixed/raw/master/netty-tcnative-boringssl-static-2.0.35.Final.jar\",' ./onos/deps/deps.json
	@sed -i '/sigar/c\    \"sigar\": \"https://oss.sonatype.org/service/local/repositories/snapshots/content/org/paleozogt/osgi/sigar/1.6.5_02-SNAPSHOT/sigar-1.6.5_02-20200407.223918-4.jar\",' ./onos/deps/deps.json
	@sed -i 's/96d9c14ab4c47cbad7fec9bdb083917db971d3754d6c7fa89f958bc719e230ed/7974c6caca8381a2efad8dc4d7e56a9ff27e19db441682950336b326f3944df5/g' ./onos/tools/build/bazel/generate_workspace.bzl
	@sed -i '/.netty-tcnative-boringssl-static-2.0.25.Final.jar/c\            \jar_urls = ["https://github.com/thanhledev/netty-tcnative-boringssl-static-fixed/raw/master/netty-tcnative-boringssl-static-2.0.35.Final.jar"],)' ./onos/tools/build/bazel/generate_workspace.bzl
	@sed -i 's/5107e6b19b1d0ff2cfcd3baf0c25f0d444330273b1f2bec710e127c733f11455/6f9f386a81581c0a5426a560b05f2c8f6363d7596bd5a4c642765d1c7d6f4f83/g' ./onos/tools/build/bazel/generate_workspace.bzl
	@sed -i '/.sigar-1.6.5_01.jar/c\            \jar_urls = ["https://oss.sonatype.org/service/local/repositories/snapshots/content/org/paleozogt/osgi/sigar/1.6.5_02-SNAPSHOT/sigar-1.6.5_02-20200407.223918-4.jar"],)' ./onos/tools/build/bazel/generate_workspace.bzl
	@sed -i 's/mvn:org.knowhowlab.osgi:sigar:jar:1.6.5_01/mvn:org.paleozogt.osgi:sigar:jar:1.6.5_02/g' ./onos/tools/build/bazel/generate_workspace.bzl
	@sed -i 's/mvn:io.netty:netty-tcnative-boringssl-static:jar:2.0.25.Final/mvn:io.netty:netty-tcnative-boringssl-static:jar:2.0.35.Final/g' ./onos/tools/build/bazel/generate_workspace.bzl

	#-init builder of buildx
	@docker run --rm --privileged docker/binfmt:$(BINFMT)
	@docker buildx create --name onos_builder
	@docker buildx use onos_builder
	@docker buildx inspect --bootstrap

build:
	@echo "Build function"
	#-build onos source code	
	@cd ./onos && bazelisk build onos --jobs 4 \
		  --verbose_failures \
		  --define profile=default
	@cp ./onos/bazel-bin/onos.tar.gz .
	@docker login -u $(DOCKER_USER) -p $(DOCKER_PASS) docker.io
	@docker buildx build --build-arg VERSION=$(VERSION) \
			--platform $(PLATFORMS) \
			--push \
			-t $(DOCKER_USER)/$(REPO):$(VERSION) .
	@docker logout
clean:
	@echo "Clean function"
	@rm -rf ./onos | true
	@rm -f ${ONOS_VERSION}.tar.gz | true
	@rm -f onos.tar.gz | true
	@docker buildx rm onos_builder | true