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

PROJECTS="lang.java lang.java.test lang.java.javac lang.java.statics lang.java.statics.test lang.java.example"

for project in $PROJECTS; do
  sed -i '' "s/$OLD_VERSION-SNAPSHOT/$NEW_VERSION/g" $project/metaborg.yaml
done

for project in . $PROJECTS; do
  sed -i '' "s|<version>.*</version>|<version>$SPOOFAX_VERSION</version>|g" $project/.mvn/extensions.xml
done

sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION.qualifier/g" lang.java.eclipse.site/site.xml
sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION.qualifier/g" lang.java.eclipse.feature/feature.xml
sed -i '' "s/$OLD_VERSION.qualifier/$NEW_VERSION.qualifier/g" lang.java.eclipse/META-INF/MANIFEST.MF

# Update Spoofax to newest released version
for project in . $PROJECTS; do
  mvn -f $project/pom.xml versions:update-parent -DgenerateBackupPoms=false
done
# Update java-front version to new version
mvn -f pom.xml versions:set -DnewVersion=$NEW_VERSION_SNAPSHOT -DprocessAllModules=true -DgenerateBackupPoms=false
mvn -f lang.java.statics.test/pom.xml versions:set -DnewVersion=$NEW_VERSION_SNAPSHOT -DgenerateBackupPoms=false # manual as long as disabled in pom.xml
