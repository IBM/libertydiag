#!/bin/sh
# /*******************************************************************************
#  * (c) Copyright IBM Corporation 2022.
#  *
#  * Licensed under the Apache License, Version 2.0 (the "License");
#  * you may not use this file except in compliance with the License.
#  * You may obtain a copy of the License at
#  *
#  *    https://www.apache.org/licenses/LICENSE-2.0
#  *
#  * Unless required by applicable law or agreed to in writing, software
#  * distributed under the License is distributed on an "AS IS" BASIS,
#  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  * See the License for the specific language governing permissions and
#  * limitations under the License.
#  *******************************************************************************/
# 

BUILDER="$1"
IMAGE="$2"
echo "Running: ${BUILDER} manifest inspect ${IMAGE}"
CURRENTMANIFESTS="$(${BUILDER} manifest inspect ${IMAGE} 2>/dev/null)"
if [ "${CURRENTMANIFESTS}" != "" ]; then
  if command -v jq >/dev/null 2>&1 && command -v tr >/dev/null 2>&1 && command -v sed >/dev/null 2>&1 ; then

    # First see if there are any manifests at all
    CHECK="$(echo "${CURRENTMANIFESTS}" | jq '.manifests')"
    if [ "${CHECK}" != "null" ]; then
      echo "Removing old images from manifest"
      for MANIFEST in $(echo "${CURRENTMANIFESTS}" | jq '.manifests[].digest' | tr '\n' ' ' | sed 's/"//g'); do
        if ${BUILDER} manifest remove ${IMAGE} ${MANIFEST} >/dev/null; then
          echo "Removed ${MANIFEST} from ${IMAGE}"
        else
          echo "Failed to remove ${MANIFEST} from ${IMAGE}. Full inspection:"
          echo "${CURRENTMANIFESTS}"
          exit 1
        fi
      done
    fi
  else
    echo "jq, tr, and sed commands must be installed"
    exit 1
  fi
fi
exit 0
