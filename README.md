### onos-multiarch
ONOS system with multi-architectures compatibly
- `ONOS`: version **2.4.0**
- `crma`: Cloudless Resource Monitoring Application running in ONOS.
- Dockerfile: base Dockerfile to build ONOS docker image supported multiple CPU architecture (current: linux/amd64 & linux/arm64)
- Makefile: provided functions for CircleCI
- .circleci/config.yml: CircleCI configuration for CI/CD.
- VERSION: current crma version.

#### How to use:
- clone project.
- make necessary changes of crma. **NOTE: update value in VERSION**
- commit and push. CircleCI will handle the rest.
