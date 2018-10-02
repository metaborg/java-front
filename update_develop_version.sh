#!/usr/bin/env sh

set -eu

if [ "$#" -eq "0" ]; then
  echo "USAGE:\n    $0 OLD_VERSION NEW_VERSION SPOOFAX_VERSION\n\nReplaces \${OLD_VERSION}-SNAPSHOT with \${NEW_VERSION}-SNAPSHOT, updates Spoofax to latest snapshot version. \n\${SPOOFAX_VERSION}-SNAPSHOT should also be this latest release version to update a file with that cannot be auto-updated with maven. "
  exit 0
fi

OLD_VERSION=$1
OLD_VERSION_SNAPSHOT="$OLD_VERSION-SNAPSHOT"
NEW_VERSION=$2
NEW_VERSION_SNAPSHOT="$NEW_VERSION-SNAPSHOT"
SPOOFAX_VERSION="$3-SNAPSHOT"

sed -i '' "s/$OLD_VERSION_SNAPSHOT/$NEW_VERSION_SNAPSHOT/g" lang.java/metaborg.yaml
sed -i '' "s/$OLD_VERSION_SNAPSHOT/$NEW_VERSION_SNAPSHOT/g" lang.java.example/metaborg.yaml
sed -i '' "s/$OLD_VERSION_SNAPSHOT/$NEW_VERSION_SNAPSHOT/g" lang.java.test/metaborg.yaml
sed -i '' "s|<version>.*</version>|<version>$SPOOFAX_VERSION</version>|g" .mvn/extensions.xml

sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION.qualifier/g" lang.java.eclipse.site/site.xml
sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION.qualifier/g" lang.java.eclipse.feature/feature.xml
sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION.qualifier/g" lang.java.eclipse/META-INF/MANIFEST.MF

# Update Spoofax to newest released version
mvn -f pom.xml versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false
mvn -f lang.java/pom.xml versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false
mvn -f lang.java.example/pom.xml versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false
mvn -f lang.java.test/pom.xml versions:update-parent -DallowSnapshots=true -DgenerateBackupPoms=false
# Update java-front version to new version
mvn -f pom.xml versions:set -DnewVersion=$NEW_VERSION_SNAPSHOT -DprocessAllModules=true -DgenerateBackupPoms=false