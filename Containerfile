# We use IBM Java 8 so that we can use the Health Center sampling profiler
FROM icr.io/appcafe/open-liberty:kernel-slim-java8-ibmjava-ubi

# These defaults are overridden by `--build-arg` arguments in pom.xml
ARG NAME=placeholder
ARG VERSION=0.0.0
ARG REVISION=placeholder
ARG AUTHORS=placeholder@example.com
ARG VENDOR=Placeholder
ARG SUMMARY=Placeholder
ARG DESCRIPTION=Placeholder
ARG URL=https://example.com/
ARG SOURCE=https://example.com/
ARG HTTP_PORT=9080
ARG HTTPS_PORT=9443
# https://spdx.org/licenses/
ARG LICENSE="Apache-2.0"

# Set some container configuration. See:
# https://github.com/OpenLiberty/ci.docker#logging
# https://openliberty.io/docs/latest/log-trace-configuration.html#settings
ARG WLP_LOGGING_CONSOLE_FORMAT=JSON
ARG WLP_LOGGING_CONSOLE_LOGLEVEL=info
ARG WLP_LOGGING_CONSOLE_SOURCE=message,trace,accessLog,ffdc,audit

ENV WLP_LOGGING_CONSOLE_FORMAT="${WLP_LOGGING_CONSOLE_FORMAT}" \
    WLP_LOGGING_CONSOLE_LOGLEVEL="${WLP_LOGGING_CONSOLE_LOGLEVEL}" \
    WLP_LOGGING_CONSOLE_SOURCE="${WLP_LOGGING_CONSOLE_SOURCE}"

# https://github.com/opencontainers/image-spec/blob/main/annotations.md#pre-defined-annotation-keys
LABEL \
  org.opencontainers.image.authors="${AUTHORS}" \
  org.opencontainers.image.vendor="${VENDOR}" \
  org.opencontainers.image.url="${URL}" \
  org.opencontainers.image.source="${SOURCE}" \
  org.opencontainers.image.version="${VERSION}" \
  org.opencontainers.image.revision="${REVISION}" \
  vendor="${VENDOR}" \
  name="${NAME}" \
  org.opencontainers.image.title="${NAME}" \
  version="${VERSION}-${REVISION}" \
  summary="${SUMMARY}" \
  description="${DESCRIPTION}" \
  org.opencontainers.image.description="${DESCRIPTION}" \
  maintainer="${AUTHORS}" \
  org.opencontainers.image.authors="${AUTHORS}" \
  org.opencontainers.image.licenses="${LICENSE}" \
  license="${LICENSE}"

# /config/configDropins/defaults/keystore.xml is overwritten directly instead of being put
# in overrides/keystore.xml to avoid the CWWKG0102I message
COPY --chown=default:root src/main/liberty/config /config

# Remove redundant config
RUN rm /config/configDropins/defaults/open-default-port.xml

# This script will add the requested XML snippets to enable Liberty features and grow image to be fit-for-purpose using featureUtility. 
# Only available in 'kernel-slim'. The 'full' tag already includes all features for convenience.
RUN features.sh

COPY --chown=default:root target/*.war /config/apps

# Maven generates a variables file that will override the defaults
COPY --chown=default:root target/liberty/wlp/usr/servers/*/configDropins/overrides/liberty-plugin-variable-config.xml /config/configDropins/overrides/

EXPOSE ${HTTP_PORT} ${HTTPS_PORT}

# This script will add the requested server configurations, apply any interim fixes and populate caches to optimize runtime
RUN configure.sh
