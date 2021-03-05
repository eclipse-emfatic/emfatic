#!/bin/bash

# Bumps Emfatic to a new version
# Requires xmlstarlet to be installed (sudo apt install xmlstarlet)

export VERSION="$1"

if [[ -z "$VERSION" ]]; then
    echo "Usage: $0 [version without .qualifier nor -SNAPSHOT]"
    exit 1
fi

update_poms_subdir() {
    find "$1" -name pom.xml | (
        while read f; do
            update_xml /pom:project/pom:parent/pom:version "$VERSION-SNAPSHOT" "$f"
        done
    )
}

update_xml() {
    XPATH="$1"
    VALUE="$2"
    shift 2
    xmlstarlet ed -L -P -N "pom=http://maven.apache.org/POM/4.0.0" -u "$XPATH" -v "$VALUE" $@
}

# Plain Maven is just this one command
mvn -f pom-plain.xml versions:set -DnewVersion="$VERSION-SNAPSHOT"

# Tycho versions:set plugin does not do all we want
update_xml /pom:project/pom:version "$VERSION-SNAPSHOT" pom.xml
update_poms_subdir bundles
update_poms_subdir features
update_poms_subdir releng

# Update plugin versions
find -path '*/org.eclipse.*/*' -name MANIFEST.MF \
     -execdir sed -i "s/Bundle-Version:.*/Bundle-Version: $VERSION.qualifier/" '{}' \;

# Update features (update sites should not mention any feature versions)
find -path '*/org.eclipse.*/*' -name feature.xml | (
    while read f; do
        update_xml /feature/@version "$VERSION.qualifier" "$f"
    done
)

# Update products
find -name '*.product' -type f | (
    while read f; do
        update_xml /product/@version "$VERSION.qualifier" "$f"
    done
)
