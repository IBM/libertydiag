FROM icr.io/appcafe/websphere-liberty:full-java17-openj9-ubi

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
ARG VERBOSE=false
ARG OPENJ9_SCC=true
ARG OPENJ9_JAVA_OPTIONS=""

# Set some container configuration. See:
# https://github.com/OpenLiberty/ci.docker#logging
# https://openliberty.io/docs/latest/log-trace-configuration.html#settings
ARG WLP_LOGGING_CONSOLE_FORMAT=JSON
ARG WLP_LOGGING_CONSOLE_LOGLEVEL=info
ARG WLP_LOGGING_CONSOLE_SOURCE=message,accessLog,ffdc,audit

ENV WLP_LOGGING_CONSOLE_FORMAT="${WLP_LOGGING_CONSOLE_FORMAT}" \
    WLP_LOGGING_CONSOLE_LOGLEVEL="${WLP_LOGGING_CONSOLE_LOGLEVEL}" \
    WLP_LOGGING_CONSOLE_SOURCE="${WLP_LOGGING_CONSOLE_SOURCE}"

EXPOSE ${HTTP_PORT} ${HTTPS_PORT}

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

COPY --chown=default:root src/main/liberty/config/server.xml /config/server.xml
COPY --chown=default:root src/main/liberty/config/jvm.options /config/jvm.options
COPY --chown=default:root src/main/liberty/config/bootstrap.properties /config/bootstrap.properties
COPY --chown=default:root src/main/liberty/config/server.env /config/server.env

# Create a /serviceability directory in case the app is installed with the Liberty Operator and there is no claim mount
# https://www.ibm.com/docs/en/was-liberty/core?topic=operator-storage-serviceability
# https://www.ibm.com/docs/en/was-liberty/core?topic=resources-webspherelibertydump-custom-resource
USER root
RUN mkdir /serviceability && chmod a+rwx /serviceability

# USER default
#
# Some versions of Kubernetes do not like using a non-numeric user ID, even
# thought the 'default' user is mapped to user ID 1001. This can cause errors such as:
# 
# container has runAsNonRoot and image has non-numeric user (default), cannot verify user is non-root
# 
# Therefore we use an explicit ID
# 
USER 1001

# Add some more advanced features that InstantOn can't handle yet
COPY --chown=default:root src/main/liberty/config/configDropins/defaults/advanced.xml /config/configDropins/defaults/advanced.xml

# Copy in the WAR file
COPY --chown=default:root target/libertydiag.war /config/apps

# Maven generates a variables file that will override the defaults
COPY --chown=default:root target/liberty/wlp/usr/servers/libertydiagServer/configDropins/overrides/liberty-plugin-variable-config.xml /config/configDropins/overrides/

# We're using the full Liberty image, so no point in checking for features to install
ENV SKIP_FEATURE_INSTALL=true

# This script will add the requested XML snippets, grow image to be fit-for-purpose and apply interim fixes
RUN configure.sh
